package org.uberfire.client.workbench;


/**
 * CDI beans that implement Header are automatically created and added to the top of the Workbench screen.
 */
public interface Header extends OrderableIsWidget {

    /**
     * Returns the stacking order of this header.
     * 
     * @return the order this header should be stacked in (higher numbers closer to the top of the screen).
     */
    @Override
    int getOrder();

}
