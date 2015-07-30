package com.newrelic.agent.deps.org.apache.commons.vfs2;


/**
 * The fileCache interface. Implementations of this interface are expected to be thread safe.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public interface FilesCache
{
    /**
     * add a fileobject to the cache.
     *
     * @param file the file
     */
    void putFile(final FileObject file);

    /**
     * add a fileobject to the cache if it isn't already present.
     *
     * @param file the file
     * @return true if the file was stored, false otherwise.
     */
    boolean putFileIfAbsent(final FileObject file);

    /**
     * retrieve a file from the cache by its name.
     *
     * @param filesystem The FileSystem.
     * @param name the name
     * @return the fileobject or null if file is not cached
     */
    FileObject getFile(final FileSystem filesystem, final FileName name);

    /**
     * purge the entries corresponding to the filesystem.
     * @param filesystem The FileSystem.
     */
    void clear(final FileSystem filesystem);

    /**
     * purge the whole cache.
     */
    void close();

    /**
     * removes a file from cache.
     *
     * @param filesystem filesystem
     * @param name       filename
     */
    void removeFile(final FileSystem filesystem, final FileName name);

    /**
     * if the cache uses timestamps it could use this method to handle
     * updates of them.
     *
     * @param file filename
     */
    // public void touchFile(final FileObject file);
}

