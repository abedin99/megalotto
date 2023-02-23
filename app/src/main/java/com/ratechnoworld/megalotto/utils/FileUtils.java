package com.ratechnoworld.megalotto.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    private FileUtils() {} //private constructor to enforce Singleton pattern

    /** TAG for log messages. */
    static final String TAG = "FileUtils";
    private static final boolean DEBUG = false; // Set to true to enable logging

    public static boolean isLocalStorageDocument(Uri uri) {
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static class LocalStorageProvider extends DocumentsProvider {

        public static final String AUTHORITY = "com.ianhanniballake.localstorage.documents";

        /**
         * Default root projection: everything but Root.COLUMN_MIME_TYPES
         */
        private final String[] DEFAULT_ROOT_PROJECTION = new String[] {
                Root.COLUMN_ROOT_ID,
                Root.COLUMN_FLAGS, Root.COLUMN_TITLE, Root.COLUMN_DOCUMENT_ID, Root.COLUMN_ICON,
                Root.COLUMN_AVAILABLE_BYTES
        };
        /**
         * Default document projection: everything but Document.COLUMN_ICON and
         * Document.COLUMN_SUMMARY
         */
        private final String[] DEFAULT_DOCUMENT_PROJECTION = new String[] {
                Document.COLUMN_DOCUMENT_ID,
                Document.COLUMN_DISPLAY_NAME, Document.COLUMN_FLAGS, Document.COLUMN_MIME_TYPE,
                Document.COLUMN_SIZE,
                Document.COLUMN_LAST_MODIFIED
        };

        @Override
        public Cursor queryRoots(final String[] projection) {
            // Create a cursor with either the requested fields, or the default
            // projection if "projection" is null.
            final MatrixCursor result = new MatrixCursor(projection != null ? projection
                    : DEFAULT_ROOT_PROJECTION);
            // Add Home directory
            File homeDir = Environment.getExternalStorageDirectory();
            final MatrixCursor.RowBuilder row = result.newRow();
            // These columns are required
            row.add(Root.COLUMN_ROOT_ID, homeDir.getAbsolutePath());
            row.add(Root.COLUMN_DOCUMENT_ID, homeDir.getAbsolutePath());
            row.add(Root.COLUMN_FLAGS, Root.FLAG_LOCAL_ONLY | Root.FLAG_SUPPORTS_CREATE);
            // These columns are optional
            row.add(Root.COLUMN_AVAILABLE_BYTES, homeDir.getFreeSpace());
            // Root.COLUMN_MIME_TYPE is another optional column and useful if you
            // have multiple roots with different
            // types of mime types (roots that don't match the requested mime type
            // are automatically hidden)
            return result;
        }

        @Override
        public String createDocument(final String parentDocumentId, final String mimeType, final String displayName) {
            File newFile = new File(parentDocumentId, displayName);
            try {
                newFile.createNewFile();
                return newFile.getAbsolutePath();
            } catch (IOException e) {
                Log.e(LocalStorageProvider.class.getSimpleName(), "Error creating new file " + newFile);
            }
            return null;
        }

        @Override
        public AssetFileDescriptor openDocumentThumbnail(final String documentId, final Point sizeHint,
                                                         final CancellationSignal signal) throws FileNotFoundException {
            // Assume documentId points to an image file. Build a thumbnail no
            // larger than twice the sizeHint
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(documentId, options);
            final int targetHeight = 2 * sizeHint.y;
            final int targetWidth = 2 * sizeHint.x;
            final int height = options.outHeight;
            final int width = options.outWidth;
            options.inSampleSize = 1;
            if (height > targetHeight || width > targetWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and
                // keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / options.inSampleSize) > targetHeight
                        || (halfWidth / options.inSampleSize) > targetWidth) {
                    options.inSampleSize *= 2;
                }
            }
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(documentId, options);
            // Write out the thumbnail to a temporary file
            File tempFile;
            FileOutputStream out = null;
            try {
                tempFile = File.createTempFile("thumbnail", null, getContext().getCacheDir());
                out = new FileOutputStream(tempFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            } catch (IOException e) {
                Log.e(LocalStorageProvider.class.getSimpleName(), "Error writing thumbnail", e);
                return null;
            } finally {
                if (out != null)
                    try {
                        out.close();
                    } catch (IOException e) {
                        Log.e(LocalStorageProvider.class.getSimpleName(), "Error closing thumbnail", e);
                    }
            }
            // It appears the Storage Framework UI caches these results quite
            // aggressively so there is little reason to
            // write your own caching layer beyond what you need to return a single
            // AssetFileDescriptor
            return new AssetFileDescriptor(ParcelFileDescriptor.open(tempFile,
                    ParcelFileDescriptor.MODE_READ_ONLY), 0,
                    AssetFileDescriptor.UNKNOWN_LENGTH);
        }

        @Override
        public Cursor queryChildDocuments(final String parentDocumentId, final String[] projection, final String sortOrder) {
            // Create a cursor with either the requested fields, or the default
            // projection if "projection" is null.
            final MatrixCursor result = new MatrixCursor(projection != null ? projection
                    : DEFAULT_DOCUMENT_PROJECTION);
            final File parent = new File(parentDocumentId);
            for (File file : parent.listFiles()) {
                // Don't show hidden files/folders
                if (!file.getName().startsWith(".")) {
                    // Adds the file's display name, MIME type, size, and so on.
                    includeFile(result, file);
                }
            }
            return result;
        }

        @Override
        public Cursor queryDocument(final String documentId, final String[] projection) {
            // Create a cursor with either the requested fields, or the default
            // projection if "projection" is null.
            final MatrixCursor result = new MatrixCursor(projection != null ? projection
                    : DEFAULT_DOCUMENT_PROJECTION);
            includeFile(result, new File(documentId));
            return result;
        }

        private void includeFile(final MatrixCursor result, final File file) {
            final MatrixCursor.RowBuilder row = result.newRow();
            // These columns are required
            row.add(Document.COLUMN_DOCUMENT_ID, file.getAbsolutePath());
            row.add(Document.COLUMN_DISPLAY_NAME, file.getName());
            String mimeType = getDocumentType(file.getAbsolutePath());
            row.add(Document.COLUMN_MIME_TYPE, mimeType);
            int flags = file.canWrite() ? Document.FLAG_SUPPORTS_DELETE | Document.FLAG_SUPPORTS_WRITE
                    : 0;
            // We only show thumbnails for image files - expect a call to
            // openDocumentThumbnail for each file that has
            // this flag set
            if (mimeType.startsWith("image/"))
                flags |= Document.FLAG_SUPPORTS_THUMBNAIL;
            row.add(Document.COLUMN_FLAGS, flags);
            // COLUMN_SIZE is required, but can be null
            row.add(Document.COLUMN_SIZE, file.length());
            // These columns are optional
            row.add(Document.COLUMN_LAST_MODIFIED, file.lastModified());
            // Document.COLUMN_ICON can be a resource id identifying a custom icon.
            // The system provides default icons
            // based on mime type
            // Document.COLUMN_SUMMARY is optional additional information about the
            // file
        }

        @Override
        public String getDocumentType(final String documentId) {
            File file = new File(documentId);
            if (file.isDirectory())
                return Document.MIME_TYPE_DIR;
            // From FileProvider.getType(Uri)
            final int lastDot = file.getName().lastIndexOf('.');
            if (lastDot >= 0) {
                final String extension = file.getName().substring(lastDot + 1);
                final String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                if (mime != null) {
                    return mime;
                }
            }
            return "application/octet-stream";
        }

        @Override
        public void deleteDocument(final String documentId) {
            new File(documentId).delete();
        }

        @Override
        public ParcelFileDescriptor openDocument(final String documentId, final String mode,
                                                 final CancellationSignal signal) throws FileNotFoundException {
            File file = new File(documentId);
            final boolean isWrite = (mode.indexOf('w') != -1);
            if (isWrite) {
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            } else {
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            }
        }

        @Override
        public boolean onCreate() {
            return true;
        }
    }
}
