package com.newrelic.agent.deps.org.apache.commons.vfs2;


/**
 * Interface to the content info.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public interface FileContentInfo
{
    /**
     * the content type.
     * @return The file content type.
     */
    String getContentType();

    /**
     * the content encoding.
     * @return The file content encoding.
     */
    String getContentEncoding();
}
