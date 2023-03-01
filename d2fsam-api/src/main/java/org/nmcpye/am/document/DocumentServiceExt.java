package org.nmcpye.am.document;

import org.nmcpye.am.user.User;

import java.util.List;

/**
 * Service Interface for managing {@link Document}.
 */
public interface DocumentServiceExt {
    String ID = DocumentServiceExt.class.getName();

    String DIR = "documents";

    /**
     * Saves a Document.
     *
     * @param document the Document to save.
     * @return the generated identifier.
     */
    Long saveDocument(Document document);

    /**
     * Retrieves the Document with the given identifier.
     *
     * @param id the identifier of the Document.
     * @return the Document.
     */
    Document getDocument(Long id);

    /**
     * Retrieves the Document with the given identifier.
     *
     * @param uid the identifier of the Document.
     * @return the Document.
     */
    Document getDocument(String uid);

    /**
     * Used when removing a file reference from a Document.
     *
     * @param document
     */
    void deleteFileFromDocument(Document document);

    /**
     * Deletes a Document.
     *
     * @param document the Document to delete.
     */
    void deleteDocument(Document document);

    /**
     * Retrieves all Documents.
     *
     * @return a Collection of Documents.
     */
    List<Document> getAllDocuments();

    int getDocumentCount();

    int getDocumentCountByName(String name);

    List<Document> getDocumentsByUid(List<String> uids);

    Long getCountDocumentByUser(User user);
}
