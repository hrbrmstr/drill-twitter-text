package hrbrmstr.drill.udf;

import io.netty.buffer.DrillBuf;
import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.IntHolder;
import org.apache.drill.exec.expr.holders.NullableVarCharHolder;
import org.apache.drill.exec.vector.complex.writer.BaseWriter;
// import com.twitter.twittertext.Extractor;

import javax.inject.Inject;

@FunctionTemplate(
  names = { "tw_parse_tweet" },
  scope = FunctionTemplate.FunctionScope.SIMPLE,
  nulls = FunctionTemplate.NullHandling.NULL_IF_NULL
)
public class ParseTweet implements DrillSimpleFunc {
  
  @Param NullableVarCharHolder input;
  
  @Output BaseWriter.ComplexWriter out;
  
  @Inject DrillBuf buffer;
    
  public void setup() {}
  
  public void eval() {
    
    org.apache.drill.exec.vector.complex.writer.BaseWriter.MapWriter mw = out.rootAsMap();
    
    String tw_string = org.apache.drill.exec.expr.fn.impl.StringFunctionHelpers.toStringFromUTF8(
       input.start, input.end, input.buffer
    );

    com.twitter.twittertext.TwitterTextParseResults parsed =
      com.twitter.twittertext.TwitterTextParser.parseTweet(tw_string);

    mw.integer("weightedLength").writeInt(parsed.weightedLength);
    mw.integer("permillage").writeInt(parsed.permillage);
    mw.bit("isValid").writeBit(parsed.isValid ? 1 : 0);
    mw.integer("display_start").writeInt(parsed.displayTextRange.start);
    mw.integer("display_end").writeInt(parsed.displayTextRange.end);
    mw.integer("valid_start").writeInt(parsed.validTextRange.start);
    mw.integer("valid_end").writeInt(parsed.validTextRange.end);
    
  }
  
}
