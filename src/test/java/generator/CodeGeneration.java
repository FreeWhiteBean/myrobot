package generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;

/**
 * @ClassName: CodeGeneration
 * @Description: 代码生成器
 */
public class CodeGeneration {

    /**
     * @param args
     * @Title: main
     * @Description: 生成
     */
    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
//        gc.setOutputDir("D:\\WorkSpaces\\pubinfo\\code\\locating-platform\\src\\main\\java");
        gc.setOutputDir(relativeJavaPath());
        gc.setFileOverride(true);
        gc.setActiveRecord(false);// 不需要ActiveRecord特性的请改为false
        gc.setEnableCache(false);// XML 二级缓存
        gc.setBaseResultMap(true);// XML ResultMap
        gc.setBaseColumnList(false);// XML columList
        gc.setAuthor("baicx");// 作者

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setControllerName("%sController");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("Woaiwojia_22");
        dsc.setUrl("jdbc:mysql://121.4.76.243:3306/simple_robot?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setTablePrefix(new String[] { "t_" });// 此处可以修改为您的表前缀
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        strategy.setInclude(new String[]{"t_author","t_class","t_macro"}); // 需要生成的表

        strategy.setSuperServiceClass(null);
        strategy.setSuperServiceImplClass(null);
        strategy.setSuperMapperClass(null);

        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("simbot.demo");
        pc.setController("controller");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setMapper("dao");
        pc.setEntity("entity");
        pc.setXml("xml");
        mpg.setPackageInfo(pc);

        // 执行生成
        mpg.execute();

    }


    private static String relativeJavaPath() {
        File file=projectPath();
        String rt = "";
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File currentFile = files[i];
            String name = currentFile.getName();
            if (name.equals("src")) {
                File srcFiles[] = currentFile.listFiles();
                for (int i1 = 0; i1 < srcFiles.length; i1++) {
                    currentFile = srcFiles[i1];
                    name = currentFile.getName();
                    if (name.equals("main")) {
                        File mainFiles[] = currentFile.listFiles();
                        for (int i2 = 0; i2 < mainFiles.length; i2++) {
                            currentFile = mainFiles[i2];
                            name = currentFile.getName();
                            if (name.equals("java")) {
                                rt = currentFile.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        return rt;
    }

    private static File projectPath() {
        String path = new Object() {
            public String getPath() {
                return this.getClass().getResource("/").getPath();
            }
        }.getPath().substring(1);
        File file = new File(path);
        file = file.getParentFile().getParentFile();
        return file;
    }
}