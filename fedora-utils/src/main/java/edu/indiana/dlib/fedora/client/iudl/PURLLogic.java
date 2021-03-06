/**
 * Copyright 2015 Trustees of Indiana University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE TRUSTEES OF INDIANA UNIVERSITY ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE TRUSTEES OF INDIANA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of the Trustees of Indiana University.
 */
package edu.indiana.dlib.fedora.client.iudl;

/**
 * <p>
 *   A class with all static methods with all the simple 
 *   functions to encapsulate the business logic rules 
 *   associated with the various identifiers and PURLs.
 *   Using this class won't improve performance, or even
 *   reduce the line count, but it might make the semantic
 *   meaning of the PURLs generated by your code more 
 *   meaningful to developers not familiar with the PURL
 *   construction rules for the DLP.
 * </p>
 * <p>
 *   PURLs are constructed in the following format:
 *   <blockquote>
 *     http://purl.dlib.indiana.edu/iudl/[collectionId]/[formatId]/[itemId]
 *   </blockquote>
 * </p>
 */
public class PURLLogic {

    /**
     * The base URL for all PURLs in this system.
     */
    private static final String PURL_ROOT = "http://purl.dlib.indiana.edu/iudl";
    
    /**
     * Gets the item Id from a PURL (in any format).
     * @param purl the PURL containing the item identifier.
     * @return the item identifier
     */
    public static String getItemIdFromPURL(String purl) {
        return purl.substring(purl.lastIndexOf('/') + 1);
    }

    /**
     * Gets the item ID from the "default view" purl.  The
     * "default view" purl is a PURL without a format identifier.
     * @param purl the PURL containing the item identifier.
     * @return the item identifier
     */
    public static String getItemIdFromDefaultPURL(String purl) {
        return purl.substring(purl.lastIndexOf('/') + 1);
    }
    
    /**
     * Gets the full Item identifier from the "default view" purl.
     * @param purl the "default view" purl for an item
     * @return the full item identifier (/[collection identifier]/[item identifier])
     * from the purl
     */
    public static String getFullItemIdFromDefaultPURL(String purl) {
        return purl.replace(PURL_ROOT, "");
    }
    
    /**
     * Gets the "default view" PURl for the given item in the given
     * collection.
     * @param collectionId the identifier for the collection
     * @param itemId the identifier for the item
     * @return
     */
    public static String getDefaultPURL(String collectionId, String itemId) {
        return PURL_ROOT + "/" + collectionId + "/" + itemId;
    }
    
    public static String getDefaultPURL(String fullItemId) {
        return PURL_ROOT + fullItemId;
    }
    
    public static String getPURL(String collectionId, String formatId, String itemId) {
        return PURL_ROOT + "/" + collectionId + "/" + formatId + "/" + itemId;
    }
    
    public static String getPURL(String fullItemId, String formatId) {
        String itemId = fullItemId.substring(fullItemId.lastIndexOf('/') + 1);
        String collectionId = fullItemId.substring(1, fullItemId.lastIndexOf('/'));
        return getPURL(collectionId, formatId, itemId);
    }
    
    public static boolean couldBePURL(String potentialPURL) {
        return potentialPURL.startsWith(PURL_ROOT);
    }

    public static String getCollectionIdFromDefaultPURL(String purl) {
        return purl.substring(PURL_ROOT.length() + 1, purl.lastIndexOf('/'));
    }
    
}
