package com.ixxus.sar.model;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;

/**
 * Created by Ixxus on 11/9/2015.
 */
public class SARContentModel {
    public static final String NAMESPACE_PREFIX = "sar";
    public static final String NAMESPACE_URI = "http://www.ixxus.com/sar/model/content/1.0";

    private SARContentModel() {}

    public static final class Aspect {
        private Aspect() {}

        public static final class RelatedTo {
            public static final QName QNAME = sar("relatedTo");
            private RelatedTo() {}
            public static final class Assoc {
                public static final class RelatedToContent extends SARClass {
                    public static final QName QNAME = sar("relatedToContent");
                    public static final QName TARGET_TYPE = ContentModel.TYPE_CONTENT;
                    public static final String TITLE = "Is Related To Content";
                    public RelatedToContent() {}
                }
                public static final class RelatedToFolder extends SARClass{
                    public static final QName QNAME = sar("relatedToFolder");
                    public static final QName TARGET_TYPE = ContentModel.TYPE_FOLDER;
                    public static final String TITLE = "Is Related To Folder";
                    public RelatedToFolder() {}
                }

                private Assoc() {}
            }

        }
    }
    private static QName sar(String localName) {
        return QName.createQName(NAMESPACE_URI, localName);
    }
    public static class SARClass {
        public static final QName QNAME = sar("test");
        public static final QName TARGET_TYPE = ContentModel.TYPE_CMOBJECT;
        public static final String TITLE = "testTitle";
    }
}
