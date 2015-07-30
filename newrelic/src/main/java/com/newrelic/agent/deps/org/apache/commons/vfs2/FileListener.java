package com.newrelic.agent.deps.org.apache.commons.vfs2;

/**
 * Listens for changes to a file.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public interface FileListener
{
    /**
     * Called when a file is created.
     * @param event The FileChangeEvent.
     * @throws Exception if an error occurs.
     */
    void fileCreated(FileChangeEvent event) throws Exception;

    /**
     * Called when a file is deleted.
     * @param event The FileChangeEvent.
     * @throws Exception if an error occurs.
     */
    void fileDeleted(FileChangeEvent event) throws Exception;

    /**
     * Called when a file is changed.<br />
     * This will only happen if you monitor the file using {@link FileMonitor}.
     * @param event The FileChangeEvent.
     * @throws Exception if an error occurs.
     */
    void fileChanged(FileChangeEvent event) throws Exception;
}
