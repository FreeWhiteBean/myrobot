package simbot.demo.service.impl;

import simbot.demo.entity.Author;
import simbot.demo.dao.AuthorMapper;
import simbot.demo.service.AuthorService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author baicx
 * @since 2021-01-19
 */
@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorMapper, Author> implements AuthorService {

}
