package simbot.demo.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author baicx
 * @since 2021-01-19
 */
@Data
public class MacroVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer ID;

    private String author;

    private String meta_1;

    private String post_subtype;

    private String post_title;


}
