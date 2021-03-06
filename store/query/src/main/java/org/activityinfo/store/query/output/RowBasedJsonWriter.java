package org.activityinfo.store.query.output;

import com.google.gson.stream.JsonWriter;
import org.activityinfo.model.query.ColumnSet;
import org.activityinfo.model.query.ColumnView;
import org.activityinfo.model.type.geo.Extents;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Writes a {@code ColumnSet} to an array of JSON Objects
 */
public class RowBasedJsonWriter {

    private final JsonWriter writer;

    public RowBasedJsonWriter(JsonWriter writer) {
        this.writer = writer;
    }

    public RowBasedJsonWriter(Writer writer) {
        this.writer = new JsonWriter(writer);
    }

    public RowBasedJsonWriter(OutputStream outputStream, Charset charset) {
        this.writer = new JsonWriter(new OutputStreamWriter(outputStream, charset));
    }

    public void write(ColumnSet columnSet) throws IOException {

        int numRows = columnSet.getNumRows();
        int numCols = columnSet.getColumns().size();
        FieldWriter[] writers = createWriters(columnSet);

        writer.beginArray();
        for(int rowIndex=0;rowIndex<numRows;++rowIndex) {

            writer.beginObject();
            for(int colIndex=0;colIndex!=numCols;++colIndex) {
                writers[colIndex].write(rowIndex);
            }
            writer.endObject();
        }
        writer.endArray();
    }

    public void flush() throws IOException {
        writer.flush();
    }

    private interface FieldWriter {
        void write(int rowIndex) throws IOException;
    }
    
    private FieldWriter[] createWriters(ColumnSet columnSet) {
        FieldWriter[] writers = new FieldWriter[columnSet.getColumns().size()];
        int index = 0;
        for (Map.Entry<String, ColumnView> column : columnSet.getColumns().entrySet()) {
            writers[index] = createWriter(column.getKey(), column.getValue());
            index++;
        }
        return writers;
    }

    public FieldWriter createWriter(final String id, final ColumnView view) {

        switch(view.getType()) {
            case STRING:
                return new FieldWriter() {
                    @Override
                    public void write(int rowIndex) throws IOException {
                        String value = view.getString(rowIndex);
                        if(value != null) {
                            writer.name(id);
                            writer.value(view.getString(rowIndex));
                        }
                    }
                };

            case NUMBER:
                return new FieldWriter() {
                    @Override
                    public void write(int rowIndex) throws IOException {
                        double value = view.getDouble(rowIndex);
                        if(!Double.isNaN(value)) {
                            writer.name(id);
                            writer.value(value);
                        }
                    }
                };
            case BOOLEAN:
                return new FieldWriter() {
                    @Override
                    public void write(int rowIndex) throws IOException {
                        int value = view.getBoolean(rowIndex);
                        if(value != ColumnView.NA) {
                            writer.name(id);
                            writer.value(value != 0);
                        }
                    }
                };
            
            case GEOGRAPHIC:
                return new FieldWriter() {
                    @Override
                    public void write(int rowIndex) throws IOException {
                        Extents extents = view.getExtents(rowIndex);
                        writer.name(id);
                        writer.beginObject();
                        writer.name("extents");
                        writer.beginArray();
                        writer.value(extents.getX1());
                        writer.value(extents.getY1());
                        writer.value(extents.getX2());
                        writer.value(extents.getY2());
                        writer.endArray();
                        writer.endObject();
                    }
                };
        }
        throw new IllegalArgumentException("type: " + view.getType());
    }
}
