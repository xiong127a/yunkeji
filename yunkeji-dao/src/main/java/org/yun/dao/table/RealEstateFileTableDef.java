package org.yun.dao.table;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.table.TableDef;

public class RealEstateFileTableDef extends TableDef {
    
    public static final RealEstateFileTableDef REAL_ESTATE_FILE = new RealEstateFileTableDef();
    
    public final QueryColumn ID = new QueryColumn(this, "id");
    public final QueryColumn QUERY_RECORD_ID = new QueryColumn(this, "query_record_id");
    public final QueryColumn FILE_TYPE = new QueryColumn(this, "file_type");
    public final QueryColumn FILE_NAME = new QueryColumn(this, "file_name");
    public final QueryColumn FILE_PATH = new QueryColumn(this, "file_path");
    public final QueryColumn FILE_SIZE = new QueryColumn(this, "file_size");
    public final QueryColumn CREATED_AT = new QueryColumn(this, "created_at");
    
    public RealEstateFileTableDef() {
        super("yunkeji", "real_estate_file");
    }
}