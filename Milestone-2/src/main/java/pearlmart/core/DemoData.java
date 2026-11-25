package pearlmart.core;
import java.io.*;import java.nio.charset.StandardCharsets;import java.math.BigDecimal;
public final class DemoData {
    public static void load(PearlMartFacade app) throws IOException {
        try(var in=DemoData.class.getResourceAsStream("/sample-data.csv")){
            if(in==null)throw new IOException("Missing sample-data.csv");try(var reader=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8))){reader.readLine();String line;while((line=reader.readLine())!=null){String[] f=line.split(",",-1);app.register(new InventoryItem(f[0],f[1],f[2],new BigDecimal(f[3]),Integer.parseInt(f[4]),f[5],f[6],null,"Sample item"));}}}
    }
}
