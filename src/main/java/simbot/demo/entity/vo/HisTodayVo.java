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
public class HisTodayVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String today;

    private String content = "历史上的今天是平凡的一天。";


    public HisTodayVo(String today) {
        this.today = today;
    }
}
