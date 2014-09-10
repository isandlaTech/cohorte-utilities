package org.psem2m.utilities.logging;

import java.util.logging.Level;

import org.psem2m.utilities.IXDescriber;

/**
 * @author ogattaz
 * 
 */
public interface IActivityLogger extends IActivityLoggerBase, IXDescriber {

    /**
     *  
     */
    void close();

    /**
     * @return
     */
    IActivityRequester getRequester();

    /**
     * @param aLevel
     */
    void setLevel(Level aLevel);

    /**
     * @param aLevelName
     */
    void setLevel(String aLevelName);

}
