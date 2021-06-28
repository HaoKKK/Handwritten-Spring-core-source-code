package springMVC.XML;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

//解析通过xml文件来确定扫描路径
public class XmlParser {
    public static String getBasePackage(String xml){
        //    通过dom4j来解析配置文件，没掌握的同学可以大致学一下
//        就是一个dom树的解析过程，Spring源码中有非常详细的解析过程，我们用dom4j协助一下
        try {
            SAXReader saxReader = new SAXReader();
//            从resources中读取流
            InputStream inputStream = XmlParser.class.getClassLoader().getResourceAsStream(xml);
//            将流读成文件
            Document document = saxReader.read(inputStream);
//            获取文件的根元素
            Element rootElement = document.getRootElement();
//            从根元素开始扫描，获得名字为component-scan的元素
            Element componentScan = rootElement.element("component-scan");
//          获取其中的base-package
            Attribute attribute = componentScan.attribute("base-package");
//            得到其中的值作为address
            String address = attribute.getText();
            return address;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }
}
