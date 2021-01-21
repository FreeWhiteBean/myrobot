package simbot.demo.entity;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author baicx
 * @since 2021-01-19
 */
@TableName("t_macro")
public class Macro implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 版本
     */
    private String version;
    /**
     * 名称
     */
    private String name;
    /**
     * 速度
     */
    private Integer speed;
    /**
     * 描述
     */
    private String desc;
    /**
     * 内容
     */
    private String info;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "Macro{" +
        ", id=" + id +
        ", version=" + version +
        ", name=" + name +
        ", speed=" + speed +
        ", desc=" + desc +
        ", info=" + info +
        "}";
    }
}
