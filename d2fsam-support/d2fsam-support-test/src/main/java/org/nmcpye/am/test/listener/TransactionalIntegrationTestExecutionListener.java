package org.nmcpye.am.test.listener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.transaction.TestContextTransactionUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
public class TransactionalIntegrationTestExecutionListener extends AbstractTestExecutionListener {

    private static final ThreadLocal<MyTransactionContext> currentTransactionContext =
        new NamedInheritableThreadLocal<>("Test Transaction Context");

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        log.info("Start Transaction - beforeTestClass");
        PlatformTransactionManager tm = TestContextTransactionUtils.retrieveTransactionManager(testContext, null);
        TransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = tm.getTransaction(txDefinition);
        MyTransactionContext txContext = new MyTransactionContext(testContext, txDefinition, txStatus, tm);

        TransactionalIntegrationTestExecutionListener.currentTransactionContext.set(txContext);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        log.info("flush - beforeTestExecution");
        MyTransactionContext txContext = TransactionalIntegrationTestExecutionListener.currentTransactionContext.get();
        txContext.getTransactionManager().rollback(txContext.getTxStatus());
//        txContext.getTxStatus().flush();
    }

//    @Override
//    public void afterTestExecution(TestContext testContext) throws Exception {
//        log.info("Rollback Transaction - afterTestMethod");
//        MyTransactionContext txContext = TransactionalIntegrationTestExecutionListener.currentTransactionContext.get();
//        txContext.getTransactionManager().rollback(txContext.getTxStatus());
//    }

    @Data
    static class MyTransactionContext {
        private final TestContext testContext;

        private final TransactionDefinition transactionDefinition;

        private final TransactionStatus txStatus;

        private final PlatformTransactionManager transactionManager;
    }
}
