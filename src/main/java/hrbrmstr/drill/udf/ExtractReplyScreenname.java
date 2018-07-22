package hrbrmstr.drill.udf;

import io.netty.buffer.DrillBuf;
import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.NullableVarCharHolder;
import org.apache.drill.exec.expr.holders.VarCharHolder;
// import com.twitter.twittertext.Extractor;

import javax.inject.Inject;

@FunctionTemplate(
  names = { "tw_extract_reply_screenname" },
  scope = FunctionTemplate.FunctionScope.SIMPLE,
  nulls = FunctionTemplate.NullHandling.NULL_IF_NULL
)
public class ExtractReplyScreenname implements DrillSimpleFunc {
  
  @Param NullableVarCharHolder input;
  
  @Output VarCharHolder out;
  
  @Inject DrillBuf buffer;
    
  public void setup() {}
  
  public void eval() {
        
    String tw_string = org.apache.drill.exec.expr.fn.impl.StringFunctionHelpers.toStringFromUTF8(
       input.start, input.end, input.buffer
    );

    com.twitter.twittertext.Extractor extractor = new com.twitter.twittertext.Extractor();
    String extracted = extractor.extractReplyScreenname(tw_string);

    byte[] result = null;

    if (extracted != null) {
      result = extracted.getBytes();
      int outputSize = result.length;
      buffer = out.buffer = buffer.reallocIfNeeded(outputSize);
      out.buffer = buffer;
      out.start = 0;
      out.end = result.length;
      buffer.setBytes(0, result, 0, outputSize);
    } else {
      buffer = out.buffer = buffer.reallocIfNeeded(0);
      out.buffer = buffer;
      out.start = 0;
      out.end = 0;
      buffer.setBytes(0, result, 0, 0);      
    }
  }
  
}
