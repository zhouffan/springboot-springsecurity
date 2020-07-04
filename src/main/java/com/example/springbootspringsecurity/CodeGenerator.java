package com.example.springbootspringsecurity;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

/**
 * 代码生成器类
 * https://mybatis.plus/guide/generator.html#%E7%BC%96%E5%86%99%E9%85%8D%E7%BD%AE
 * @author
 * @version 1.0
 * @date 2020/3/23 15:53
 */
public class CodeGenerator {
    private static Logger logger = LoggerFactory.getLogger(CodeGenerator.class);
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        Map<String, Object> objectMap = get("/jdbc.yml");
        String driverClass = (String) objectMap.get("jdbc.driverClass");
        String url = (String) objectMap.get("jdbc.url");
        String userId = (String) objectMap.get("jdbc.userId");
        String password = (String) objectMap.get("jdbc.password");
        logger.info("===> "+password);
        logger.info("===> "+CodeGenerator.class.getPackage().getName());
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        final String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("zhouxfu");
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        //实体属性 Swagger2 注解
        gc.setSwagger2(true);

        // 是否打开输出目录 默认为true
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        dsc.setDriverName(driverClass);
        dsc.setUsername(userId);
        dsc.setPassword(password);
        mpg.setDataSource(dsc);

        // 包配置
        final PackageConfig pc = new PackageConfig();
        // pc.setModuleName(scanner("模块名"));
//        pc.setParent("com.amt.demoonetable");
        pc.setParent(CodeGenerator.class.getPackage().getName()); //当前类的全路径名

        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<FileOutConfig>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        // 指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // strategy.setSuperEntityClass("com.fame.common.BaseEntity");
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // strategy.setSuperControllerClass("com.fame.common.BaseController");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
//        strategy.setSuperEntityColumns("id");
        // strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("sys");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }



    //存储配置属性的Map集合
    public static Map<String, Object> conf = new HashMap<String, Object>();

    /**
     * 获取yml
     * @param resourcePathName
     * @return
     */
    public static Map<String, Object> get(String resourcePathName) {
        logger.info(resourcePathName);
        //从classpath下获取配置文件路径
        URL url = CodeGenerator.class.getResource(resourcePathName);
        Yaml yaml = new Yaml();
        //通过yaml对象将配置文件的输入流转换成map原始map对象
        Map map = null;
        try {
            map = yaml.loadAs(new FileInputStream(url.getPath()), Map.class);

            //递归map对象将配置加载到conf对象中
            loadRecursion(map, "");
            return conf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    //递归解析map对象
    private static void loadRecursion(Map<String, Object> map, String key){
        map.forEach((k,v) -> {
            if(isParent(v)){
                Map<String, Object> nextValue = (Map<String, Object>) v;
                loadRecursion(nextValue, (("".equals(key) ? "" : key + ".")+ k));
            }else{
                conf.put(key+"."+k,v);
            }
        });
    }

    //判断是否还有子节点
    private static boolean isParent(Object o){
        if (!(o instanceof String || o instanceof Character || o instanceof Byte)) {
            try {
                Number n = (Number) o;
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }
}