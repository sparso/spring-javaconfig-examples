/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import java.util.Date;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import uk.co.parso.barebones.entities.Test;
import uk.co.parso.barebones.repositories.TestRepository;

/**
 *
 * @author sam
 */
@Service
public class TestServiceImpl implements TestService {
    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private TestRepository testRepo;

    @RequestMapping("test")
    @ResponseStatus(HttpStatus.OK)
    @Transactional(readOnly=false,isolation=Isolation.SERIALIZABLE,propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
    @Override
    public void test() {
        logger.debug("TEST");
        Test test = testRepo.selectForUpdate(1L);
        Test test2 = testRepo.selectForUpdate(2L);
        test.setName("test" + (new Date()).getTime());
        test.setName("test2" + (new Date()).getTime());
    }
}
