package hrbrmstr.drill.udf;

import io.netty.buffer.DrillBuf;
import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.NullableVarCharHolder;
import org.apache.drill.exec.vector.complex.writer.BaseWriter;
// import com.twitter.twittertext.Extractor;

import javax.inject.Inject;

@FunctionTemplate(
  names = { "tw_extract_screennames" },
  scope = FunctionTemplate.FunctionScope.SIMPLE,
  nulls = FunctionTemplate.NullHandling.NULL_IF_NULL
)
public class ExtractScreennames implements DrillSimpleFunc {
  
  @Param NullableVarCharHolder input;
  
  @Output BaseWriter.ComplexWriter out;
  
  @Inject DrillBuf buffer;
    
  public void setup() {}
  
  public void eval() {
    
    org.apache.drill.exec.vector.complex.writer.BaseWriter.ListWriter mw = out.rootAsList();
    
    String tw_string = org.apache.drill.exec.expr.fn.impl.StringFunctionHelpers.toStringFromUTF8(
       input.start, input.end, input.buffer
    );

    com.twitter.twittertext.Extractor extractor = new com.twitter.twittertext.Extractor();
    java.util.List<String> extracted = extractor.extractMentionedScreennames(tw_string);

    mw.startList();

    for (String url : extracted) {

      byte[] outBytes = url.getBytes();
      
      buffer.reallocIfNeeded(outBytes.length); 
      buffer.setBytes(0, outBytes);

      mw.varChar().writeVarChar(0, outBytes.length, buffer); 

    }

    mw.endList();
    
  }
  
}
