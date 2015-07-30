package com.newrelic.agent.deps.org.apache.commons.vfs2;
/**
 * FileMonitor interface.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public interface FileMonitor
{
    /**
     * Adds a file to be monitored.
     * @param file The FileObject to monitor.
     */
    void addFile(final FileObject file);

    /**
     * Removes a file from being monitored.
     * @param file The FileObject to stop monitoring.
     */
    void removeFile(final FileObject file);
}
