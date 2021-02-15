package prosayj.framework.common.utils.security.crypto.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 对日志中的某些信息做掩码显示
 *
 * @author yangjian@bubi.cn
 * @date 2020-04-02 上午 10:53
 * @since 1.0.0
 */
public class LoggerMaskUtils {

    /**
     * 需要掩码的字段名
     */
    private static final List<String> MASKED_INFO = Arrays.asList(
            "cardno", "card_no");

    private static final String xmlHead = "<?xml version=\"1.0\" encoding=\"GBK\"?>";

    /**
     * 掩码符号
     */
    private static final String CARD_MASKED_SYMBOL = "**** ****";
    /**
     * 掩码符号
     */
    private static final String MOBILE_MASKED_SYMBOL = "****";
    /**
     * 银行卡号正则
     */
    private static final String CARD_PATTREN = "^[0-9]{6,25}$";
    /**
     * 身份证正则15位
     */
    private static final String FIFTEEN_CERT_PATTREN = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    /**
     * 身份证正则18位
     */
    private static final String EIGHTEEN_CERT_PATTREN = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
    /**
     * 手机号正则
     */
    private static final String MOBILE_PATTREN = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$";

    /**
     * @param orgStrName    字段名
     * @param orgStrContent 字段内容
     * @return 需要掩码：掩码后的字段内容；不需要掩码：字段原内容
     * @Description: 对传入字段的字段名检查，如果需要掩码，则返回掩码的字段内容，否则返回原字段内容
     */
    public static String checkAndMaskString(String orgStrName, String orgStrContent) {
        if (StringUtils.isBlank(orgStrName) || StringUtils.isBlank(orgStrContent)) {
            return orgStrContent;
        }
        if (MASKED_INFO.contains(orgStrName)
                || MASKED_INFO.contains(orgStrName.toLowerCase())
                || MASKED_INFO.contains(orgStrName.toUpperCase())) {
            return getMaskedString(orgStrContent);
        }

        return orgStrContent;
    }

    /**
     * @param orgStrContent 原字符串
     * @return 如果是卡号，则返回掩码后的字符串，否则，返回原字符串
     * @Description: 对传入的卡号或包含卡号的xml格式, obj格式的内容做卡号的掩码操作，规则，保留前六后四
     */
    public static String maskString(String orgStrContent) {
        try {
            if (StringUtils.isBlank(orgStrContent)) {
                return orgStrContent;
            }
            if (isXmlType(orgStrContent)) {
                return getMaskedXmlStr(orgStrContent);
            } else if (isObjectType(orgStrContent)) {
                return getMaskedObjectStr(orgStrContent);
            } else if (isJsonType(orgStrContent)) {
                return getMaskedJsonStr(orgStrContent);
            } else {
                return getMaskedString(orgStrContent);
            }
        } catch (Exception e) {
            return orgStrContent;
        }
    }

    /**
     * 判断字符串是否json格式
     *
     * @param orgStrContent
     * @return
     */
    private static boolean isJsonType(String orgStrContent) {
        try {
            JSONObject.parse(orgStrContent);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static String getMaskedLineString(String orgStrContent) {
        StringBuffer resultStr = new StringBuffer("");
        String[] elementArry = orgStrContent.split("\\|");
        for (int i = 0; i < elementArry.length; i++) {
            String key = elementArry[i].substring(0, elementArry[i].indexOf("="));
            if (key.toLowerCase().contains("cvv")) {
                resultStr.append(key).append("=***|");
                continue;
            }
            if (MASKED_INFO.contains(key)) {
                String value = elementArry[i].substring(elementArry[i].indexOf("=") + 1);
                resultStr.append(key).append("=").append(maskString(value)).append("|");
            } else {
                resultStr.append(elementArry[i]).append("|");
            }
        }
        String result = resultStr.toString();
        return result.substring(0, result.length() - 1);
    }

    /**
     * 对json格式字符串的敏感字段做掩码并返回，目前只支持敏感字段在第一层节点的格式
     *
     * @param orgStrContent
     * @return
     */
    private static String getMaskedJsonStr(String orgStrContent) {
        JSONObject jsonObj = JSONObject.parseObject(orgStrContent);
        int keyWordSize = MASKED_INFO.size();
        for (int j = 0; j < keyWordSize; j++) {
            if (jsonObj.containsKey(MASKED_INFO.get(j))) {
                jsonObj.put(MASKED_INFO.get(j), maskString(jsonObj.get(MASKED_INFO.get(j)).toString()));
                if (MASKED_INFO.get(j).toLowerCase().contains("cvv")) {
                    jsonObj.put(MASKED_INFO.get(j), "***");
                }
            }
        }
        return jsonObj.toJSONString();
    }

    /**
     * @param orgStrContent 字符串
     * @return 掩码后的字符串
     * @Description: 对xml格式的字符串的卡号字段做掩码操作
     */
    private static String getMaskedXmlStr(String orgStrContent) {
        String resXmlContent = "";
        SAXReader reader = new SAXReader();

        List<Element> resList = new ArrayList<Element>();
        try {
            String encodingType = getEncodingOfXml(orgStrContent);
            Document doc = reader.read(new ByteArrayInputStream(orgStrContent.getBytes(encodingType)));
            Element rootEl = doc.getRootElement();
            parseAndSelectNode(rootEl, resList);
            for (Element e : resList) {
                if (StringUtils.equals(e.getQName().getName(), "Plain")) {
                    e.setText(getMaskedLineString(e.getText()));
                    continue;
                }
                System.out.println(e.getText());
                e.setText(maskString(e.getText()));
                if (e.getQName().getName().toLowerCase().contains("cvv")) {
                    e.setText("***");
                }

            }
            resXmlContent = doc.asXML();
        } catch (Exception e) {
            return orgStrContent;
        }
        return resXmlContent;
    }

    /**
     * @param node    遍历的节点
     * @param resList 存放结果的list
     * @Description: 遍历并查找符合条件的节点，并放到list中
     */
    private static void parseAndSelectNode(Element node, List<Element> resList) {
        for (Iterator i = node.elementIterator(); i.hasNext(); ) {
            Element element = (Element) i.next();
            if (element.elements().size() > 0) {
                parseAndSelectNode(element, resList);
            } else {
                if (StringUtils.equals(element.getName(), "Plain")) {
                    resList.add(element);
                    continue;
                }
                int keyWordSize = MASKED_INFO.size();
                for (int j = 0; j < keyWordSize; j++) {
                    if (StringUtils.equals(element.getName(), MASKED_INFO.get(j))) {
                        resList.add(element);
                    }
                }
            }
        }
    }

    /**
     * @param orgStrContent 字符串
     * @return 掩码后的字符串
     * @Description: 对object格式的字符串的卡号字段做掩码操作
     */
    private static String getMaskedObjectStr(String orgStrContent) {
        orgStrContent = deletePlainMessage(orgStrContent);
        String resStrContent = new String(orgStrContent);
        Map<String, String> keyResMap = getKeyResMap(orgStrContent);
        Iterator it = keyResMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = (Entry) it.next();
            resStrContent = resStrContent.replaceAll(entry.getKey(), entry.getValue());
        }
        return resStrContent;
    }

    private static String deletePlainMessage(String text) {
        int startIndex = text.indexOf("plainMessage");
        if (startIndex < 0) {
            return text;
        }
        int endIndex = text.indexOf(",", startIndex);
        if (endIndex < 0) {
            endIndex = text.indexOf("]", startIndex);
        }
        String str = text.substring(startIndex, endIndex);
        return text.replace(str, "plainMessage=*****");
    }

    /**
     * @param orgContent 对象字符串
     * @return 需要掩码的子串组成的map，key为源字符串中需要掩码的字串，value为掩码后的字串；
     * @Description: 从对象字符串中获取需要掩码的子串组成的map；
     */
    private static Map<String, String> getKeyResMap(String orgContent) {
        Map<String, String> resMap = new HashMap<String, String>();
        int keyWordSize = MASKED_INFO.size();
        for (int j = 0; j < keyWordSize; j++) {
            String tmpContent = new String(orgContent);
            String startStr = MASKED_INFO.get(j) + "=";
            int index = 0;
            while (tmpContent.length() > startStr.length() && index >= 0) {
                index = tmpContent.indexOf(startStr);
                if (index >= 0) {
                    tmpContent = tmpContent.substring(index);
                    int endIndex = getEndIndex(tmpContent);
                    String keyWord = tmpContent.substring(tmpContent.indexOf(startStr), endIndex);
                    String value = startStr + maskString(SensitiveInfoUtils.smartDecryptWithCheck(keyWord.substring(startStr.length())));
                    if (startStr.toLowerCase().contains("cvv")) {
                        value = startStr + "***";
                    }
                    resMap.put(keyWord, value);
                    tmpContent = tmpContent.substring(keyWord.length());
                }
            }
        }
        return resMap;
    }

    /**
     * @param content 字符串
     * @return 是：true，否：false
     * @Description: 判断判断字符串是否xml格式
     */
    private static boolean isXmlType(String content) {
        if (content.contains("<?xml")) {
            return true;
        }
        return false;
    }

    /**
     * @param content 字符串
     * @return 是：true，否：false
     * @Description: 字符串是否由对象反射组成
     */
    private static boolean isObjectType(String content) {
        if (content.contains("=") && content.contains(",")) {
            return true;
        }
        return false;
    }

    /**
     * @param content 原内容
     * @return 第一个属性结束的下标值
     * @Description: 对象类型的字符串内容，取第一个属性结束的下标
     * 如"bankNum=123456789,bankNam=......"，返回","的下标值
     */
    private static int getEndIndex(String content) {
        int commaIndex = content.indexOf(",");
        int bracketIndex = content.indexOf("]");
        int braceIndex = content.indexOf("}");

        //返回三个下标中最小且大于0的
        int realIndex = content.length();
        if (commaIndex > 0 && commaIndex < realIndex) {
            realIndex = commaIndex;
        }
        if (bracketIndex > 0 && bracketIndex < realIndex) {
            realIndex = bracketIndex;
        }
        if (braceIndex > 0 && braceIndex < realIndex) {
            realIndex = braceIndex;
        }

        return realIndex;
        /*if(commaIndex > 0 && commaIndex < bracketIndex &&  commaIndex < braceIndex){
            return commaIndex;
		} else if(bracketIndex > 0 && bracketIndex < braceIndex && bracketIndex < commaIndex){
			return bracketIndex;
		} else if(braceIndex > 0 && braceIndex < commaIndex && braceIndex < bracketIndex){
			return braceIndex;
		} else {
			return content.length();
		}*/
    }

    /**
     * @param orgStr 原字符串
     * @return 掩码后的字符串
     * @Description: 对传入的字符串做掩码操作，卡号身份证号保留前六后四，手机号保留前三后四，中间用符号代替
     */
    private static String getMaskedString(String orgStr) {
        if (StringUtils.isBlank(orgStr)) {
            return orgStr;
        }
        String dstStr = orgStr.replaceAll("\\s*", "");
        if (isLikeMobile(dstStr)) {
            return getMaskedMobile(orgStr);
        }
        if (!isLikeCardNo(dstStr) && !isLikeCertNo(dstStr)) {
            return orgStr;
            //如果不是卡号，则判断是否手机号并处理
            //return getMaskedMobile(orgStr);
        }
        int length = dstStr.length();
        // 大于12位，显示前6后4，等于12位，前4后4，其他情况，显示后面length/2长度
        if (length > 12) {
            dstStr = dstStr.substring(0, 6) + CARD_MASKED_SYMBOL + dstStr.substring(dstStr.length() - 4, dstStr.length());
        } else if (length == 12) {
            dstStr = dstStr.substring(0, 4) + CARD_MASKED_SYMBOL + dstStr.substring(dstStr.length() - 4, dstStr.length());
        } else {
            dstStr = CARD_MASKED_SYMBOL + dstStr.substring(dstStr.length() - length / 2, dstStr.length());
        }

        return dstStr;
    }

    /**
     * @param orgMobile 原手机号
     * @return 掩码后的手机号
     * @Description: 对传入的手机号做掩码操作，保留前三后四，中间用符号代替
     */
    private static String getMaskedMobile(String orgMobile) {
        if (StringUtils.isBlank(orgMobile)) {
            return orgMobile;
        }
        String dstMobile = orgMobile.replaceAll("\\s*", "");
        dstMobile = dstMobile.substring(0, 3) + MOBILE_MASKED_SYMBOL + dstMobile.substring(dstMobile.length() - 4, dstMobile.length());

        return dstMobile;
    }

    /**
     * 判断是否手机号
     *
     * @param mobileStr 输入字符串
     * @return 如果是返回true，否则false
     */
    public static boolean isLikeMobile(String mobileStr) {
        return mobileStr.matches(MOBILE_PATTREN);
    }

    /**
     * @param content 内容
     * @return 校验结果，符合：true，不符合：false
     * @Description: 使用正则表达式，对内容校验是否符合卡号
     */
    private static boolean isLikeCardNo(String content) {
        return content.matches(CARD_PATTREN);
    }

    /**
     * @param certNo 内容
     * @return 校验结果，符合：true，不符合：false
     * @Description: 使用正则表达式，对内容校验是否符合身份证号
     */
    private static boolean isLikeCertNo(String certNo) {
        return certNo.matches(FIFTEEN_CERT_PATTREN) || certNo.matches(EIGHTEEN_CERT_PATTREN);
    }

    /**
     * 获取xml报文的encoding方式，
     *
     * @param xml xml报文
     * @return 字符串的编码方式
     */
    private static String getEncodingOfXml(String xml) {
        String beginEncodingMark = "encoding=";
        int beginIndex = xml.indexOf(beginEncodingMark) + beginEncodingMark.length() + 1;
        String endEncodingMark = "?>";
        //int endIndex = xml.indexOf(endEncodingMark) - 1;
        int endIndex = xml.indexOf("\"", beginIndex);

        if (beginIndex > 0 && endIndex > 0 && endIndex > beginIndex) {
            if ("GB2312".equalsIgnoreCase(xml.substring(beginIndex, endIndex))) {
                return "GBK";
            } else {
                return xml.substring(beginIndex, endIndex);
            }
        }
        return "utf-8";
    }

    /**
     * @param orgStr
     * @return
     * @Description: 跨境卡号解码并掩码
     */
    public static String cbrmbBankCardMaskString(String orgStr) {
        orgStr = SensitiveInfoUtils.smartDecryptWithCheck(orgStr);
        if (StringUtils.isBlank(orgStr)) {
            return orgStr;
        }
        String dstStr = orgStr.replaceAll("\\s*", "");

        int length = dstStr.length();
        // 大于12位，显示前6后4，等于12位，前4后4，其他情况，显示后面length/2长度
        if (length > 12) {
            dstStr = dstStr.substring(0, 6) + "**** ****" + dstStr.substring(dstStr.length() - 4, dstStr.length());
        } else if (length == 12) {
            dstStr = dstStr.substring(0, 4) + "**** ****" + dstStr.substring(dstStr.length() - 4, dstStr.length());
        } else {
            dstStr = "**** ****" + dstStr.substring(dstStr.length() - length / 2, dstStr.length());
        }

        return dstStr;
    }


    public static void main(String[] args) {
        String sendCertification = "<Fastpay><Message id=\"1512983840219\"><FPAReq id=\"FPAReq\"><certType>1</certType><certNo>220724199104011822</certNo><cardType>D</cardType><cardNo>6216260000018804147</cardNo><mobilePhone>13522441895</mobilePhone></FPAReq></Message></Fastpay> ";
        System.out.println(maskString(sendCertification));
        String sendData = "<Fastpay><Message id=\"1512983101549\"><CPReq id=\"CPReq\"><instId>1248</instId><serialNo>2017121105187691</serialNo><date>20171211 17:04:20</date><signNo>FA201709120001913350</signNo><amount>0.01</amount><currency>156</currency><pyeeNm>国付宝信息科技有限公司</pyeeNm><pyeeAcctId>11012296147101</pyeeAcctId><pyeeAcctTp>0</pyeeAcctTp><pyeeAcctIssrId>1248</pyeeAcctIssrId><mrchntTp>1</mrchntTp><mrchntCertTp>11</mrchntCertTp><mrchntCertId>0000000000</mrchntCertId><ordrDesc>2017121105187691</ordrDesc></CPReq><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">" +
                "<ds:SignedInfo>" +
                "<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"></ds:CanonicalizationMethod>" +
                "<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></ds:SignatureMethod>" +
                "<ds:Reference URI=\"#CPReq\">" +
                "<ds:Transforms>" +
                "<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></ds:Transform>" +
                "</ds:Transforms>" +
                "<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></ds:DigestMethod>" +
                "<ds:DigestValue>2vrvy6hYCQ5tAqRdWj8rgBMAxwA=</ds:DigestValue>" +
                "</ds:Reference>" +
                "</ds:SignedInfo>" +
                "<ds:SignatureValue>" +
                "n3+cTuYsTasSinL0XdadPmHv/4Ex7ktCqB0fEFE/kaKqAeLtQqe5IOsI8Ac+Tv4XTvrwcHF5G6vT" +
                "7TjCvYL2xcTpxnBptcG/X4NAgl3e7Uup18OCpdahDqoCY1YR5fWoQZFMCp4q50h+3mwBnq1ekjha" +
                "1IBHaNbxC53r4fBA8fc=" +
                "</ds:SignatureValue>" +
                "</ds:Signature></Message></Fastpay> ";
        String receiveData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Fastpay><Message id=\"1512983101549\"><CPRes id=\"CPRes\"><serialNo>2017121105187691</serialNo><signNo>6216260000018804147</signNo><overdraft>N</overdraft><Extension/><instId>1248</instId><remark>国付宝</remark><bankSerialNo>1187071712110721107912</bankSerialNo><amount>0.01</amount><TokenNo>FA201709120001913350</TokenNo><currency>156</currency><liquDate>20171211</liquDate></CPRes><Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><SignedInfo><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/><Reference URI=\"#CPRes\"><Transforms><Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/><DigestValue>JyG4jg9KBPqmPQ7zQqSVw/1RjCc=</DigestValue></Reference></SignedInfo><SignatureValue>mBJ9oZrGOiGkl0Ek5TSDAqIYNDTUWmHk108Oe5OGODe0vHMuW5fh/d/mlnVuYeE4B0LlsVAnRAIy" +
                "Tn/X7DlLEixC7u9mQWsX6xbfr7OKO46/UzkDvG1X1K2tNwa3fSunanlVtV809VTrfttvMtxC2lU8" +
                "3mQ6PdjhCmPRoZ1WOCU=</SignatureValue></Signature></Message></Fastpay>";
        String test = "com.gopay.dps.domain.bean.paychanne" +
                ".AcctOrCardBean@53f5f0c[sq=150050,accountId=223456789789877568,certNo=142325198703180013," +
                "avaAmt=0,isCredit=1,isVcc=1,firstChoice=<null>,bankCode=TBANK,bankDesc=测试银行储蓄卡" +
                ",amtLimt=10000,IsQuota=<null>,cardStatus=1,bindMobile=13439751284]";
        String jsonStr = "{\"gopayOrderId\":\"2017011205226378\",\"recvCustId\":\"0000004147\"," +
                "\"orderIp\":\"127.0.0.1\",\"payChannel\":\"03\",\"cvv2\":\"444\",\"bindMobile\":\"13552838095\"," +
                "\"amt\":\"20.01\",\"gopayIntTxnCd\":\"00100\",\"payCustId\":" +
                "\"0000005720\",\"strategyList\":[{\"refuseContinue\":false,\"engine\":" +
                "\"realTimeEngine\",\"handleService\":\"blackListHandleService\"},{\"refuseContinue\"" +
                ":false,\"engine\":\"realTimeEngine\",\"handleService\":\"g0001HandleService\"},{" +
                "\"refuseContinue\":false,\"engine\":\"realTimeEngine\",\"handleService\":\"hG0001HandleService" +
                "\"},{\"refuseContinue\":true,\"engine\":\"nearRealTimeEngine\",\"handleService\":\"msgProductionHandleService" +
                "\"},{\"refuseContinue\":true,\"engine\":\"nearRealTimeEngine\",\"handleService\":\"comRuleHandleService\"" +
                "},{\"refuseContinue\":true,\"engine\":\"nearRealTimeEngine\",\"handleService\":\"hG0002HandleService\"},{" +
                "\"refuseContinue\":true,\"engine\":\"nearRealTimeEngine\",\"handleService\":\"g0003HandleService\"},{\"refuseCo" +
                "ntinue\":true,\"engine\":\"nearRealTimeEngine\",\"handleService\":\"reportHandleService\"}]}";

        String ttStr = "com.gopay.gateway.domain.parameters.RequProData@699fcb96[xmlEncode=<null>," +
                "transSq=97788,cvv2=567,redirectType=FUND,tranType=110,tranCode=8203," +
                "version=2.0,gatewaySource=FUND_,packetData={refererDomain=webtesttest1.gopay.com.cn, " +
                "gopayServerTime=20170602162058, signType=1, certificateNo=142325198703180013, " +
                "type=1, version=2.0, bankNo=6222020200113130716, merOrderNum=F_4697_9, " +
                "verficationCode=11111aaaaa, refer=http://webtesttest1.gopay.com.cn/webtest/fund/doBindCard.do, merchantId=0000009002, " +
                "pageSize=100, headerIP=210.13.252.2, charset=2, tranIP=127.0.0.1, certificateType=1, " +
                "signValue=430d3ebc862664fa77c3c9ce3873d11b, bankCode=ICBC, tranDateTime=20170602153945, source=p," +
                " merRemark2=, tranCode=8203, backgroundMerUrl=http://webtesttest1.gopay.com.cn/webtest/result/webclient/background.do," +
                " realName=å¼ è´µå¾, merRemark1=," +
                " agentSignNo=2000900217060100000231, mobile=15210268230},clientIp=<null>,sessionId=<null>]";
        String retString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Fastpay><Message id=\"1495704823441\"><SOQRes id=\"SOQRes\"><cvv2>555</cvv2><orderDate>20170525</orderDate><transType>1</transType><signNo>345</signNo><amount>0.01</amount><currency>156</currency><status>Y</status><cause>银行主机单笔代收成功！</cause></SOQRes></Message></Fastpay>";
        String s = " <sign><?xml version=\"1.0\" encoding=\"GBK\" ?>" +
                "<CMS>\n" +
                "    <eb>\n" +
                "        <pub>\n" +
                "            <TransCode>PAYENT</TransCode>\n" +
                "            <CIS>020090004200540</CIS>\n" +
                "            <BankCode>102</BankCode>\n" +
                "            <ID>GFBYZ005.y.0200</ID>\n" +
                "            <TranDate>20171117</TranDate>\n" +
                "            <TranTime>175001234</TranTime>\n" +
                "            <fSeqno>201711171750000099557959649311</fSeqno>\n" +
                "        </pub>\n" +
                "        <in>\n" +
                "            <OnlBatF>1</OnlBatF>\n" +
                "            <SettleMode>0</SettleMode>\n" +
                "            <TotalNum>1</TotalNum>\n" +
                "            <TotalAmt>1</TotalAmt>\n" +
                "            <SignTime>20171117175001235</SignTime>\n" +
                "            <ReqReserved1></ReqReserved1>\n" +
                "            <ReqReserved2></ReqReserved2>\n" +
                "            <rd>\n" +
                "                <iSeqno>2017111708283664</iSeqno>\n" +
                "                <ReimburseNo></ReimburseNo>\n" +
                "                <ReimburseNum></ReimburseNum>\n" +
                "                <StartDate></StartDate>\n" +
                "                <StartTime></StartTime>\n" +
                "                <PayType>2</PayType>\n" +
                "                <PayAccNo>0200059029200307204</PayAccNo>\n" +
                "                <PayAccNameCN></PayAccNameCN>\n" +
                "                <PayAccNameEN></PayAccNameEN>\n" +
                "                <RecAccNo>6212260200080903814</RecAccNo>\n" +
                "                <RecAccNameCN></RecAccNameCN>\n" +
                "                <RecAccNameEN></RecAccNameEN>\n" +
                "                <SysIOFlg>1</SysIOFlg>\n" +
                "                <IsSameCity>1</IsSameCity>\n" +
                "                <Prop>0</Prop>\n" +
                "                <RecICBCCode></RecICBCCode>" +
                " <RecCityName></RecCityName>\n" +
                "                <RecBankNo>102</RecBankNo>\n" +
                "                <RecBankName></RecBankName>\n" +
                "                <CurrType>001</CurrType>\n" +
                "                <PayAmt>1</PayAmt>\n" +
                "                <UseCode></UseCode>\n" +
                "                <UseCN>2017111708283664</UseCN>\n" +
                "                <EnSummary></EnSummary>\n" +
                "                <PostScript>2017111708283664</PostScript>\n" +
                "                <Summary>2017111708283664</Summary>\n" +
                "                <Ref></Ref>\n" +
                "                <Oref></Oref>\n" +
                "                <ERPSqn></ERPSqn>\n" +
                "                <BusCode></BusCode>\n" +
                "                <ERPcheckno></ERPcheckno>\n" +
                "                <CrvouhType></CrvouhType>\n" +
                "                <CrvouhName></CrvouhName>\n" +
                "                <CrvouhNo></CrvouhNo>\n" +
                "                <BankType></BankType>\n" +
                "                <FileNames></FileNames>\n" +
                "                <Indexs></Indexs>\n" +
                "                <PaySubNo></PaySubNo>\n" +
                "                <RecSubNo></RecSubNo>\n" +
                "                <MCardNo></MCardNo>\n" +
                "                <MCardName></MCardName>\n" +
                "            </rd>\n" +
                "        </in>\n" +
                "    </eb>\n" +
                "</CMS></sign> ";
        System.out.println(s.indexOf(">"));

		/*System.out.println(jsonObj.get("refuseContinue"));
		System.out.println(jsonObj.get("cardNo"));
		jsonObj.put("cardNo", maskString(jsonObj.get("cardNo").toString()));
		System.out.println(jsonObj.get("cardNo"));*/
        //System.out.println(maskString(jsonStr));
        //System.out.println(jsonObj.toJSONString());
        //System.out.println(maskString(test));
    }

    /**
     * @param userName 姓名
     * @return 掩码后的 姓名
     * @Description: 姓名掩码
     */
    public static String maskNameString(String userName) {
        String name = "";
        if (StringUtils.isNotBlank(userName)) {
            if (userName.length() >= 2) {
                StringBuffer sb = new StringBuffer();
                sb.append("");
                for (int i = 0; i < userName.length() - 1; i++) {
                    sb = sb.append("*");
                }
                name = sb.append(userName.substring(userName.length() - 1, userName.length())).toString();
            } else {
                name = userName;
            }
        }
        return name;
    }


    /**
     * 网关批量付款到银行子订单银行卡号掩码处理
     *
     * @param orgStrName    字段名
     * @param orgStrContent 字段内容
     * @return 需要掩码：掩码后的字段内容；不需要掩码：字段原内容
     */
    public static String checkGatewayAndMaskString(String orgStrName, String orgStrContent) {
        if (StringUtils.isBlank(orgStrName) || StringUtils.isBlank(orgStrContent)) {
            return orgStrContent;
        }
        if ("batchSubOrder".contains(orgStrName)) {
            return getMaskedBankNoString(orgStrContent);
        }
        return orgStrContent;
    }

    /**
     * 网关批量付款到银行子订单银行卡号掩码处理
     *
     * @param orgStr 原字符串
     * @return
     */
    private static String getMaskedBankNoString(String orgStr) {
        String patternStr = "\\|(\\[)?([0-9]{6,25}+)(\\])?\\|";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(orgStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String dstStr = matcher.group().replaceAll("[|]", "");
            String value = getMaskedString(dstStr);
            matcher.appendReplacement(sb, "|" + value + "|");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param orgStrContent 原字符串
     * @return 如果是卡号，则返回掩码后的字符串，否则，返回原字符串
     * @Description: 对传入的卡号或包含卡号的xml格式, obj格式的内容做卡号的掩码操作，规则，保留前六后四
     */
    public static String addHeadMaskString(String orgStrContent) {
        String xmlContent = LoggerMaskUtils.maskString(xmlHead + orgStrContent);
        return xmlContent.replace(xmlHead, "");
    }

}
