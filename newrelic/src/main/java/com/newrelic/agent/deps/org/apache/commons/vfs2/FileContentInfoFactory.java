package com.newrelic.agent.deps.org.apache.commons.vfs2;

/**
 * Create a class which is able to determine the content-info for the given content.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public interface FileContentInfoFactory
{
    FileContentInfo create(FileContent fileContent) throws FileSystemException;
}
