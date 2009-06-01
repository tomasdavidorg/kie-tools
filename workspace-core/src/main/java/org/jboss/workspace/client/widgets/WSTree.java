package org.jboss.workspace.client.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import org.jboss.workspace.client.listeners.TabOpeningClickHandler;

import java.util.HashMap;
import java.util.Map;

public class WSTree extends Composite {
    private Tree tree;
    private long last;

    private TreeItem lastItem;
    private long threshold = 400;

    private Map<TreeItem, ClickHandler> clickListeners
            = new HashMap<TreeItem, ClickHandler>();

    public WSTree() {
        tree = new Tree();
        tree.setAnimationEnabled(true);

        /**
         * Add the mouse listener to handle double-click.
         */
        tree.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                long elapsed = System.currentTimeMillis() - last;
                if (!getUserAgent().contains("msie")) {
                    last = System.currentTimeMillis();
                    TreeItem fti = tree.getSelectedItem();

                    if (fti != null && fti.equals(lastItem) && elapsed < threshold) {
                        if (fti.getChildCount() > 0) {
                            if (!fti.getState()) {
                                fti.setState(true, true);
                            }
                            else {
                                fti.setState(false, false);
                            }
                        }
                        else {
                            clickListeners.get(fti).onClick(null);
                        }
                        last = 0;
                    }
                }
            }
        });


        tree.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                lastItem = null;
            }
        });

        tree.addMouseUpHandler(new MouseUpHandler() {
            public void onMouseUp(MouseUpEvent event) {
                               long elapsed = System.currentTimeMillis() - last;

                TreeItem fti = tree.getSelectedItem();

                if (getUserAgent().contains("msie")) {
                    last = System.currentTimeMillis();

                    if (fti != null && elapsed < threshold && lastItem != null
                            && fti.hashCode() == lastItem.hashCode()) {
                        if (fti.getChildCount() > 0) {
                            if (!fti.getState()) {
                                fti.setState(true, true);
                            }
                            else {
                                fti.setState(false, false);
                            }
                        }
                        else {
                            clickListeners.get(fti).onClick(null);
                        }
                        lastItem = null;
                    }
                    else {
                        lastItem = fti;
                    }
                }
                else {
                    lastItem = fti;
                }
            }
        });

        initWidget(tree);
    }


    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public WSTree addSubTree() {
        WSTree gTree = new WSTree();
        tree.addItem(gTree);
        return gTree;
    }

    public TreeItem addItem(Image icon, Widget content, ClickHandler handler) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(icon);
        hPanel.add(content);

        hPanel.setCellWidth(icon, "16px");

        TreeItem item = new TreeItem(hPanel);
        addItem(item, handler);
        return item;
    }

    public void addItem(TreeItem item) {
        item.getElement().getStyle().setProperty("marginTop", "1px");
        item.getElement().getStyle().setProperty("userSelect", "none");
        tree.addItem(item);
    }

    public void addItem(TreeItem item, ClickHandler handler) {
        addItem(item);
        clickListeners.put(item, handler);
    }

    public void attachListener(TreeItem item, ClickHandler handler) {
        clickListeners.put(item, handler);
    }

    public static native String getUserAgent() /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;

}
