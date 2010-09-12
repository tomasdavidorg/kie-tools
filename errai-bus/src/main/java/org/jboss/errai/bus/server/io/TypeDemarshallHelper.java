/*
 * Copyright 2010 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.errai.bus.server.io;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.common.client.types.DecodingContext;
import org.jboss.errai.common.client.types.UnsatisfiedForwardLookup;
import org.mvel2.ConversionHandler;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.mvel2.DataConversion.addConversionHandler;

public class TypeDemarshallHelper {
    static {
        addConversionHandler(java.sql.Date.class, new ConversionHandler() {
            public Object convertFrom(Object o) {
                if (o instanceof String) o = Long.parseLong((String) o);

                return new java.sql.Date(((Number) o).longValue());
            }

            public boolean canConvertFrom(Class aClass) {
                return Number.class.isAssignableFrom(aClass);
            }
        });

        addConversionHandler(java.util.Date.class, new ConversionHandler() {
            public Object convertFrom(Object o) {
                if (o instanceof String) o = Long.parseLong((String) o);
                return new java.util.Date(((Number) o).longValue());
            }

            public boolean canConvertFrom(Class aClass) {
                return Number.class.isAssignableFrom(aClass);
            }
        });
    }

    private static final Map<Class, Map<String, Serializable>> MVELDencodingCache = new ConcurrentHashMap<Class, Map<String, Serializable>>();


    public static Object demarshallAll(Object o, DecodingContext ctx) throws Exception {
        try {
            if (o instanceof String) {
                return o;

            } else if (o instanceof Collection) {
                ArrayList newList = new ArrayList(((Collection) o).size());
                Object dec;
                for (Object o2 : ((Collection) o)) {
                    newList.add(demarshallAll(o2, ctx));
                }

                if (ctx.hasUnsatisfiedDependency(o)) {
                    ctx.swapDepReference(o, newList);
                }

                return newList;
            } else if (o instanceof Map) {
                Map<?, ?> oMap = (Map) o;
                if (oMap.containsKey(SerializationParts.ENCODED_TYPE)) {
                    String objId = (String) oMap.get(SerializationParts.OBJECT_ID);
                    boolean ref = false;
                    if (objId != null) {
                        if (objId.charAt(0) == '$') {
                            ref = true;
                            objId = objId.substring(1);
                        }

                        if (ctx.hasObject(objId)) {
                            return ctx.getObject(objId);
                        } else if (ref) {
                            throw new UnsatisfiedForwardLookup(objId);
                        }
                    }

                    Class clazz = Thread.currentThread().getContextClassLoader().loadClass((String) oMap.get(SerializationParts.ENCODED_TYPE));
                    if (clazz.isEnum()) {
                        return Enum.valueOf(clazz, (String) oMap.get("EnumStringValue"));
                    }

                    Object newInstance = clazz.newInstance();
                    ctx.putObject(objId, newInstance);

                    if (ctx.hasUnsatisfiedDependency(o)) {
                        ctx.swapDepReference(o, newInstance);
                    }

                    Map<String, Serializable> s = MVELDencodingCache.get(clazz);

                    if (s == null) {
                        synchronized (MVELDencodingCache) {
                            s = MVELDencodingCache.get(newInstance.getClass());
                            if (s == null) {
                                s = new HashMap<String, Serializable>();
                                for (String key : (Set<String>) oMap.keySet()) {
                                    if (SerializationParts.ENCODED_TYPE.equals(key) || SerializationParts.OBJECT_ID.equals(key))
                                        continue;
                                    s.put(key, MVEL.compileSetExpression(key));
                                }
                            }
                            MVELDencodingCache.put(newInstance.getClass(), s);
                        }
                    }

                    for (Map.Entry<?, ?> entry : oMap.entrySet()) {
                        if (SerializationParts.ENCODED_TYPE.equals(entry.getKey()) || SerializationParts.OBJECT_ID.equals(entry.getKey()))
                            continue;
                        final Serializable cachedSetExpr = s.get(entry.getKey());
                        if (cachedSetExpr != null) {
                            try {
                                MVEL.executeSetExpression(cachedSetExpr, newInstance, demarshallAll(entry.getValue(), ctx));
                            }
                            catch (UnsatisfiedForwardLookup e) {
                                e.setPath((String) entry.getKey());
                                ctx.addUnsatisfiedDependency(newInstance, e);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        } else {
                            try {
                                MVEL.setProperty(newInstance, String.valueOf(entry.getKey()), demarshallAll(entry.getValue(), ctx));
                            }
                            catch (UnsatisfiedForwardLookup e) {
                                e.setPath((String) entry.getKey());
                                ctx.addUnsatisfiedDependency(newInstance, e);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                    }


                    return newInstance;
                }
            }
            return o;
        }
        catch (UnsatisfiedForwardLookup e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("error demarshalling encoded object:\n" + o, e);
        }
    }

    public static void resolveDependencies(DecodingContext ctx) {
        for (Map.Entry<Object, List<UnsatisfiedForwardLookup>> entry : ctx.getUnsatisfiedDependencies().entrySet()) {
            for (UnsatisfiedForwardLookup dep : entry.getValue()) {
                if (!ctx.hasObject(dep.getId()))
                    throw new RuntimeException("cannot satisfy dependency in object graph (bug?):" + dep.getId());

                if (entry.getKey() instanceof Collection) {
                    ((Collection) entry.getKey()).add(ctx.getObject(dep.getId()));
                } else {
                    if (dep.getPath() == null) {
                        throw new RuntimeException("cannot satisfy dependency in object graph (path unresolvable):" + dep.getId());
                    }
                    MVEL.setProperty(entry.getKey(), dep.getPath(), ctx.getObject(dep.getId()));
                }
            }
        }

    }
}
