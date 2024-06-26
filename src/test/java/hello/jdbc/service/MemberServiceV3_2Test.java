package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

class MemberServiceV3_2Test {

    public static final String MemberA = "memberA";
    public static final String MemberB = "memberB";
    public static final String MemberEx = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_2(memberRepository, transactionManager);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MemberA);
        memberRepository.delete(MemberB);
        memberRepository.delete(MemberEx);

    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        //given
        Member memberA = new Member(MemberA, 10000);
        Member memberB = new Member(MemberB, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //when
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        //then
        Member foundMemberA = memberRepository.findById(memberA.getMemberId());
        Member foundMemberB = memberRepository.findById(memberB.getMemberId());
        Assertions.assertThat(foundMemberA.getMoney()).isEqualTo(8000);
        Assertions.assertThat(foundMemberB.getMoney()).isEqualTo(12000);
    }
    @Test
    @DisplayName("이체 중 예외 발생하지만 트랜잭션이 정상 작동하여 롤백된다")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(MemberA, 10000);
        Member memberEx = new Member(MemberEx, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);
        //when and then
        Assertions.assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);


        Member foundMemberA = memberRepository.findById(memberA.getMemberId());
        Member foundMemberEx = memberRepository.findById(memberEx.getMemberId());
        Assertions.assertThat(foundMemberA.getMoney()).isEqualTo(10000);
        Assertions.assertThat(foundMemberEx.getMoney()).isEqualTo(10000);
    }
}