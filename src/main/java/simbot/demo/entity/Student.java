package simbot.demo.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author baicx
 * @since 2021-01-14
 */
@TableName("t_student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 学生姓名
     */
    private String name;
    /**
     * 学生年龄
     */
    private Integer age;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
        ", id=" + id +
        ", name=" + name +
        ", age=" + age +
        "}";
    }
}
