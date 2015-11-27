package com.qa.models;

import java.util.ArrayList;
import java.util.List;

public class WebApiResultSetModel
    extends WebApiBasicObjectModel
{
    public class ColumnModel{
        private String name;
        private String heading;
        private String type;
        private String detailedType;
        private String scale;
        private String precision;
        private boolean nullable;
        
        public String getName() {
            return name;
        }
        public String getHeading() {
            return heading;
        }
        public String getType() {
            return type;
        }
        public String getDetailedType() {
            return detailedType;
        }
        public String getScale() {
            return scale;
        }
        public String getPrecision() {
            return precision;
        }
        public boolean isNullable() {
            return nullable;
        }
        
        private List<ColumnModel> metadata = new ArrayList<ColumnModel>();

        public List<ColumnModel> getMetadata() {
            return metadata;
        }
        
        
    } 
}

