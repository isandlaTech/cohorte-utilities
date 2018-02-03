package org.psem2m.utilities.rsrc;

import java.nio.file.WatchEvent.Kind;

/**
 * interface that describe a handle to be call while we are notify y a change of
 * the directory
 *
 * @author apisu
 *
 */
public interface IRsrcNotifierHandler {

	public void handle(final Kind<?> aKind, final String aFileName);
}
