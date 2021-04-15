package simbot.demo.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import love.forte.catcode.CatCodeUtil;
import love.forte.catcode.CodeTemplate;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import simbot.demo.entity.Class;
import simbot.demo.entity.Student;
import simbot.demo.entity.vo.HisTodayVo;
import simbot.demo.service.ClassService;
import simbot.demo.service.IStudentService;
import simbot.demo.utils.Const;
import simbot.demo.utils.HttpUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.alibaba.fastjson.JSONPatch.OperationType.add;

/**
 * <p> 用作示例的监听器类。默认的监听器示例。
 *
 * <p> 类上需要标注@Component，因为此框架存在依赖注入功能。
 *
 * <p> 此示例类中监听函数是与群聊有关的。
 *
 * <p> 当你出现了：发送消息成功无报错、酷Q日志中也显示发送成功无报错，但是实际上机器人没有发出任何消息的时候，此时大概率是消息被屏蔽。
 * 这种情况的原因很多，例如机器人账号异地登录、等级太低、没有活跃度、很少登录、腾讯看你像是机器人等各种因素
 * 。
 * <p> 解决办法目前已知可尝试：手动登录bot账号去水群、发消息提升活跃度、多挂机两天摆脱嫌疑、提升活跃度、充值会员（不一定能行）等方法。
 *
 * @author <a href="https://github.com/FreeWhiteBean"> FreeWhiteBean </a>
 */
@Component
public class GroupListener {

    @Autowired
    private IStudentService studentService;
    @Autowired
    private ClassService classService;
    @Autowired
    private RedisTemplate redisTemplate;


    private final Map<String, String> copyMap = new HashMap<>();
    private final Map<String, String> copiedMap = new HashMap<>();
    private final Map<String, String> imageMap = new HashMap<>();
    private final Map<String, Integer> dongMap = new HashMap<>();
    private final Map<String, Map<String, Object>> macroMap = new HashMap<>();
    boolean tianmi = false;
    boolean dujitang = false;
    boolean pengyouquan = false;
    boolean zuichou = false;
    boolean liaotian = false;

    @OnGroup
    public void onGroupMsg(GroupMsg groupMsg, MsgSender sender) {

        // thisCode 代表当前接收到消息的机器人账号。
        final String botCode = groupMsg.getBotInfo().getBotCode();
        // 发消息人的群昵称或者昵称
        final String nickname = groupMsg.getAccountInfo().getAccountNickname();
//        final String nickname = "nickname";
        // 发消息人的账号
        final String code = groupMsg.getAccountInfo().getAccountCode();
        // 接收到消息的群号
        final String groupCode = groupMsg.getGroupInfo().getGroupCode();
        // 发消息人发的消息
        final String msg = groupMsg.getMsg();

        // 由于拼接的东西比较长，用java自带的MessageFormat对消息进行格式化，会比较直观
        final MessageFormat message = new MessageFormat("base机器人{0}接收到了群{1}中{2}({3})发送的群消息：{4}");

        final String printMsg = message.format(new Object[]{botCode, groupCode, nickname, code, msg});

        // utils实例
        final CatCodeUtil utils = CatCodeUtil.getInstance();
        // 获取string模板
        final CodeTemplate<String> stringTemplate = utils.getStringTemplate();
        if (Const.GROUP_LIST.contains(groupCode)) {
            init(msg);
            if (msg.contains(".help")) {
                String sb = "1.群消息发送“.甜蜜”进入甜蜜模式" + "\n" +
                        "2.群消息发送“.毒鸡汤”进入毒鸡汤模式" + "\n" +
                        "3.群消息发送“.朋友圈”进入朋友圈文案模式" + "\n" +
                        "4.群消息发送“.嘴臭”进入嘴臭模式" + "\n" +
                        "5.群消息发送“.聊天”进入自动聊天模式" + "\n" +
                        "6.群消息发送“.历史今日”查询历史今日的大事" + "\n" +
                        "7.群消息中包含“白叔”发送白叔喜欢的图片（本人亲友）" + "\n" +
                        "8.群消息中包含“来点好康的”私聊消息发送人一张好康的图片" + "\n" +
                        "9.群消息中包含“来点你懂的(”私聊消息发送人一张你懂的图片" + "\n" +
                        "10.roll点 群消息中包含“/roll”发送 1-100 的随机数" + "\n" +
                        "11.群消息发送“禁言@xxx”随机roll点，根据点数发起禁言，50以下禁言自己，50以上禁言对方" + "\n" +
                        "12.群消息中出现连续两条一样的消息自动复读" + "\n" +
                        "13.群消息发送“.闭嘴”，就闭嘴了" + "\n" +
                        "14.门派宏，群消息发送”.xx宏“可查询对应门派宏，可选项”byxxx“（非必填）,查询某作者的宏（宏来源剑三box），如”.藏剑宏by韶华“";
                sender.SENDER.sendGroupMsg(groupMsg, sb);
            }

            /** 群消息发送“.甜蜜”进入甜蜜模式*/
            reply(groupMsg, sender, tianmi, Const.QH);
            /** 群消息发送“.毒鸡汤”进入毒鸡汤模式*/
            reply(groupMsg, sender, dujitang, Const.DJT);
            /** 群消息发送“.朋友圈”进入朋友圈文案模式*/
            reply(groupMsg, sender, pengyouquan, Const.PYQ);
            /** 群消息发送“.嘴臭”进入嘴臭模式*/
            reply(groupMsg, sender, zuichou, Const.ZUICHOU);
            /** 群消息发送“.聊天”进入自动聊天模式*/
            AI(groupMsg, sender, liaotian);

            /** 群消息中包含“白叔”发送白叔喜欢的图片*/
            baishu(groupMsg, sender, msg, stringTemplate);

            /** 群消息中包含“来点好康的”私聊消息发送人一张好康的图片*/
            haokang(groupMsg, sender, msg, stringTemplate);
            /** 群消息中包含“来点你懂的(”私聊消息发送人一张你懂的图片*/
            haokang1(groupMsg, sender, msg, stringTemplate);

            /** 群消息中出现连续两条一样的消息自动复读*/
            fudu(groupMsg, sender, groupCode, msg);

            /** roll点 群消息中包含“/roll”发送 1-100 的随机数*/
            roll(groupMsg, sender, code, msg, stringTemplate);

            /** 群消息发送“禁言@xxx”随机roll点，根据点数发起禁言，50以下禁言自己，50以上禁言对方*/
            jinyan(groupMsg, sender, code, groupCode, msg, stringTemplate);

            /** 测试数据库查询*/
            student(groupMsg, sender, msg);

            /** 查询门派宏功能*/
            macro(groupMsg, sender, msg, stringTemplate);

            /** 查询历史今日的大事*/
            hisToday(groupMsg, sender, msg);
            // 红色显眼儿一点
            System.err.println(printMsg);
        }
    }

    private void hisToday(GroupMsg groupMsg, MsgSender sender, String msg) {
        if (msg.contains(".历史今日")) {
            String key = String.format(Const.HIS_TODAY_REDIS_KEY, LocalDate.now().toString());
            String data = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.isBlank(data)) {
                String dsResult = HttpUtils.httpGet(Const.HIS_TODAY, null, null);
                JSONObject jsonObject = JSON.parseObject(dsResult);
                if (jsonObject.getInteger("code") == 200) {
                    data = jsonObject.getString("data");
                    if (StringUtils.isBlank(data)){
                        HisTodayVo hisTodayVo = new HisTodayVo(LocalDate.now().toString());
                        ArrayList<HisTodayVo> hisTodayVos = new ArrayList<>();
                        hisTodayVos.add(hisTodayVo);
                        data = JSON.toJSONString(hisTodayVos);
                    }
                    redisTemplate.opsForValue().set(key, data);
                } else {
                    sender.SENDER.sendGroupMsg(groupMsg, "有点小问题，你再重新试试？");
                    return;
                }
            }
            List<JSONObject> jsonObjects = JSONArray.parseArray(data, JSONObject.class);
            StringBuilder sb = new StringBuilder();
            for (JSONObject jsonObject : jsonObjects) {
                sb.append(jsonObject.getString("today")).append("，")
                        .append(jsonObject.getString("content")).append("\n").append("\n");
            }
            sender.SENDER.sendGroupMsg(groupMsg, sb.toString());
        }
    }

    private void macro(GroupMsg groupMsg, MsgSender sender, String msg, CodeTemplate<String> stringTemplate) {
        Map<String, Object> codeMacroInfo = macroMap.get(groupMsg.getAccountInfo().getAccountCode());
        Integer isWaitMacro = 0;
        if (codeMacroInfo != null) {
            isWaitMacro = (Integer) codeMacroInfo.get("isWaitMacro");
        } else {
            codeMacroInfo = new HashMap<>();
        }
        if (isWaitMacro == null || isWaitMacro == 0) {
            if (msg.contains("宏") && msg.contains(".")) {
                String subtypes = msg.substring(msg.indexOf(".") + 1, msg.indexOf("宏"));
                String auth = "";
                if (msg.contains("by")) {
                    auth = msg.substring(msg.indexOf("by") + 2);
                }
                EntityWrapper<Class> ew = new EntityWrapper<>();
                ew.like("other_name", subtypes);
                List<Class> classList = classService.selectList(ew);
                String subtype = classList.isEmpty() ? null : classList.get(0).getName();
                String url = String.format(Const.MACRO_URL, subtype, auth);
                String resultStr = HttpUtils.httpGet(url, null, null);
                JSONObject jsonObject = JSON.parseObject(resultStr);
                JSONObject resultData = jsonObject.getJSONObject("data").getJSONArray("list").toJavaList(JSONObject.class)
                        .get(0);
                JSONObject author = resultData.getJSONObject("author");
                JSONObject post = resultData.getJSONObject("post");

                JSONArray macroArray = post.getJSONObject("post_meta").getJSONArray("data");
                List<JSONObject> macroList = macroArray.toJavaList(JSONObject.class);
                codeMacroInfo.put("author", author);
                codeMacroInfo.put("post", post);
                codeMacroInfo.put("macroList", macroList);
                codeMacroInfo.put("isWaitMacro", 1);
                macroMap.put(groupMsg.getAccountInfo().getAccountCode(), codeMacroInfo);
                StringBuilder sb = new StringBuilder();
                String image = author.getString("avatar");
                if (image != null) {
                    sb.append(stringTemplate.image(image)).append("\n");
                }
                for (int i = 0; i < macroList.size(); i++) {
                    sb.append(i + 1).append(".").append(macroList.get(i).getString("name")).append("\n");
                }
                sb.append("输入对应序号查询");

                sender.SENDER.sendGroupMsg(groupMsg, sb.toString());
            }
        } else {
            int index = 0;
            try {
                index = Integer.parseInt(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (index > 0) {
                codeMacroInfo.put("isWaitMacro", 0);
                macroMap.put(groupMsg.getAccountInfo().getAccountCode(), codeMacroInfo);
                index -= 1;
                Object authorO = codeMacroInfo.get("author");
                Map<String, String> author = JSONObject.parseObject(JSONObject.toJSONString(authorO), Map.class);
                Object postO = codeMacroInfo.get("post");
                Map<String, String> post = JSONObject.parseObject(JSONObject.toJSONString(postO), Map.class);
                List<JSONObject> macroList = (List<JSONObject>) codeMacroInfo.get("macroList");
//                List<JSONObject> macroList = JSONObject.parseObject(JSONObject.toJSONString(macroListO), JSONObject.class);
                JSONObject macro = macroList.get(index);
                String talent = macro.getString("talent");
                String qixue;
                if (StringUtils.isNotBlank(talent)) {
                    qixue = JSONObject.parseObject(talent, JSONObject.class).getString("sq");
                } else {
                    qixue = "传统奇穴";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(macro.getString("name")).append("\n");
                sb.append("适用版本：").append(post.get("meta_1")).append("\n");
                sb.append("奇穴方案：").append(qixue).append("\n");
                sb.append("用法：").append(macro.get("desc")).append("\n");
                sb.append(macro.get("macro")).append("\n");
                sb.append("作者：").append(post.get("author")).append("\n");
                sender.SENDER.sendGroupMsg(groupMsg, sb.toString());
            }
        }
    }

    private void student(GroupMsg groupMsg, MsgSender sender, String msg) {
        if (msg.contains(".学生")) {
            List<Student> students = studentService.selectList(new EntityWrapper<>());
            StringBuilder sb = new StringBuilder("学生列表：");
            for (Student student : students) {
                sb.append("\n").append(student.getName()).append(",").append(student.getAge()).append("岁");
            }
            sender.SENDER.sendGroupMsg(groupMsg, sb.toString());
        }
    }

    private void reply(GroupMsg groupMsg, MsgSender sender, boolean flag, String qh) {
        if (flag) {
            String dsResult = HttpUtils.httpGet(qh, null, null);
            assert dsResult != null;
            sender.SENDER.sendGroupMsg(groupMsg, dsResult);
        }
    }

    private void AI(GroupMsg groupMsg, MsgSender sender, boolean flag) {
        if (flag) {
            String msg = groupMsg.getMsg();
            if (msg.contains(".聊天")) {
                msg = "嗨~";
            }
            String url = String.format(Const.AI_API, msg);
            String dsResult = HttpUtils.httpGet(url, null, null);
            String content = JSONObject.parseObject(dsResult).getString("content");
            sender.SENDER.sendGroupMsg(groupMsg, content);
        }
    }

    private void init(String msg) {

        if (msg.contains(".甜蜜")) {
            tianmi = true;
            dujitang = false;
            pengyouquan = false;
            zuichou = false;
            liaotian = false;
        }
        if (msg.contains(".毒鸡汤")) {
            tianmi = false;
            dujitang = true;
            pengyouquan = false;
            zuichou = false;
            liaotian = false;
        }
        if (msg.contains(".朋友圈")) {
            tianmi = false;
            dujitang = false;
            pengyouquan = true;
            zuichou = false;
            liaotian = false;
        }
        if (msg.contains(".嘴臭")) {
            tianmi = false;
            dujitang = false;
            pengyouquan = false;
            zuichou = true;
            liaotian = false;
        }
        if (msg.contains(".聊天")) {
            tianmi = false;
            dujitang = false;
            pengyouquan = false;
            zuichou = false;
            liaotian = true;
        }
        if (msg.contains(".闭嘴")) {
            tianmi = false;
            dujitang = false;
            pengyouquan = false;
            zuichou = false;
            liaotian = false;
        }
    }

    private void jinyan(GroupMsg groupMsg, MsgSender sender, String code, String groupCode, String msg, CodeTemplate<String> stringTemplate) {
        if (msg.contains("禁言[")) {
            int i = new Random().nextInt(100);
            int type = 0;
            int time = 0;
            if (i == 0) {
                type = 3;
                time = 1200;
            }
            if (i > 0 && i <= 9) {
                type = 1;
                time = 60;
            }
            if (i >= 10 && i <= 29) {
                type = 1;
                time = 120;
            }
            if (i >= 30 && i <= 49) {
                type = 1;
                time = 180;
            }
            if (i >= 50 && i <= 69) {
                type = 2;
                time = 60;
            }
            if (i >= 70 && i <= 89) {
                type = 2;
                time = 120;
            }
            if (i >= 90 && i < 99) {
                type = 2;
                time = 180;
            }
            if (i == 99) {
                type = 4;
                time = 1200;
            }
            if (type == 1) {
                sender.SETTER.setGroupBan(groupCode, code, time);
                String s = stringTemplate.at(code) + "roll了 " + i + " 点，自己被禁言了 " + time / 60 + " 分钟";
                sender.SENDER.sendGroupMsg(groupMsg, s);
            } else {
                String banCode = msg.substring(msg.indexOf("qq=") + 3, msg.indexOf(",display"));
                if (type == 2) {
                    sender.SETTER.setGroupBan(groupCode, banCode, time);
                    String s = stringTemplate.at(code) + "roll了 " + i + " 点，把" + stringTemplate.at(banCode) + "禁言了 " + time / 60 + " 分钟";
                    sender.SENDER.sendGroupMsg(groupMsg, s);
                } else if (type == 3) {
                    sender.SETTER.setGroupBan(groupCode, code, time);
                    String s = stringTemplate.at(code) + "roll了 " + i + " 点，触发内里反弹，自己被禁言了 " + time / 60 + " 分钟";
                    sender.SENDER.sendGroupMsg(groupMsg, s);
                } else {
                    sender.SETTER.setGroupBan(groupCode, banCode, time);
                    String s = stringTemplate.at(code) + "roll了 " + i + " 点，触发了会心一击，把" + stringTemplate.at(banCode) + "禁言了 " + time / 60 + " 分钟";
                    sender.SENDER.sendGroupMsg(groupMsg, s);
                }
            }

        }
    }

    private void roll(GroupMsg groupMsg, MsgSender sender, String code, String msg, CodeTemplate<String> stringTemplate) {
        if (msg.contains("/roll")) {
            if ("704685927".equalsIgnoreCase(code)) {
                int i = new Random().nextInt(70) + 31;
                String s = stringTemplate.at(code) + " 你roll了 " + i + " 点";
                sender.SENDER.sendGroupMsg(groupMsg, s);
            } else {
                int i = new Random().nextInt(100);
                String s = stringTemplate.at(code) + " 你roll了 " + i + " 点";
                if (i < 50) {
                    s += "，菜啊弟弟~";
                }
                if (i > 95) {
                    s += "，牛啊弟弟~";
                }
                sender.SENDER.sendGroupMsg(groupMsg, s);
            }
        }
    }


    private void fudu(GroupMsg groupMsg, MsgSender sender, String groupCode, String msg) {
        if (msg.contains("mirai,url=")) {
            String result = msg.substring(msg.lastIndexOf("mirai,url=") + 10, msg.length() - 1);
            System.out.println("图片===================" + result);
            String image = HttpUtils.httpGet(result, null, null);
            assert image != null;
            if (image.equalsIgnoreCase(imageMap.get(groupCode)) && !image.equals(copiedMap.get(groupCode))) {
                sender.SENDER.sendGroupMsg(groupMsg, msg);
                imageMap.put(groupCode, "");
                copiedMap.put(groupCode, image);
            } else {
                imageMap.put(groupCode, image);
            }
        } else {
            System.out.println("last=======================" + copyMap.get(groupCode));
            System.out.println("this=======================" + msg);
            System.out.println("copied=======================" + copiedMap.get(groupCode));
            if (msg.equals(copyMap.get(groupCode)) && !msg.equals(copiedMap.get(groupCode)) && !msg.equals("/roll")) {
                sender.SENDER.sendGroupMsg(groupMsg, msg);
                copyMap.put(groupCode, "");
                copiedMap.put(groupCode, msg);
            } else {
                copyMap.put(groupCode, msg);

            }
        }
    }

    private void haokang(GroupMsg groupMsg, MsgSender sender, String msg, CodeTemplate<String> stringTemplate) {
        if (msg.contains("来点好康的")) {
            String dsResult = HttpUtils.httpGet(Const.HAOKANG, null, null);
            JSONObject jsonObject = JSON.parseObject(dsResult);
            String image = jsonObject.getString("imgurl");
//            String image = jsonObject.getString("msg");

            String s = stringTemplate.image(image);
            sender.SENDER.sendPrivateMsg(groupMsg, s);
        }
    }

    private void haokang1(GroupMsg groupMsg, MsgSender sender, String msg, CodeTemplate<String> stringTemplate) {
        String code = groupMsg.getAccountInfo().getAccountCode();
        if ("来点你懂的(".equals(msg)) {
            Integer hours = dongMap.get("hours");
            if (hours == null || hours != LocalDateTime.now().getHour()) {
                dongMap.clear();
                dongMap.put("hours", LocalDateTime.now().getHour());
                dongMap.put(code, 0);
            }
            Integer i = dongMap.get(code);
            if (i != null && i >= Const.DONG_NUM) {
                String s = stringTemplate.at(code) + " 就你小子要色图是吧？没有快滚！";
                sender.SENDER.sendGroupMsg(code, s);
            } else {
                if (i == null) {
                    dongMap.put(code, 0);
                }
                String dsResult = HttpUtils.httpGet(Const.HAOKANG1, null, null);
                JSONObject jsonObject = JSON.parseObject(dsResult);
                String images = jsonObject.getString("data");
                List<HashMap> list = JSON.parseArray(images, HashMap.class);
                String image = (String) list.get(0).get("url");
//            String image = jsonObject.getString("msg");
//            image = image.replace("https://i.pximg.net/","http://125.124.19.36:12365/htserver/");
//            String s = stringTemplate.image(image);
                sender.SENDER.sendPrivateMsg(groupMsg, image);
                dongMap.put(code, dongMap.get(code) + 1);

            }
        }
    }

    private void baishu(GroupMsg groupMsg, MsgSender sender, String msg, CodeTemplate<String> stringTemplate) {
        if (msg.contains("白叔")) {
            String dsResult = HttpUtils.httpGet(Const.BAISHU, null, null);
            JSONObject jsonObject = JSON.parseObject(dsResult);
            String image = jsonObject.getString("imgurl");

            String s = stringTemplate.image(image);
            sender.SENDER.sendGroupMsg(groupMsg, s);
        }
    }

//    public static void main(String[] args) {
//        String msg = "禁言[CQ:at,qq=1141464342,display=@fw]";
//        String banCode = msg.substring(msg.indexOf("qq=") + 3, msg.indexOf(",display"));
//        System.out.println(banCode);
//    }

}
