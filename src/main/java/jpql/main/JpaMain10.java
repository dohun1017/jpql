package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain10 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            em.flush();
            em.clear();

            /**
             * 벌크 연산 - update, delete 라고 생각하면 된다.(특정 건에 대한 update, delete 제외)
             *      쿼리 한 번으로 여러 테이블 로우 변경(엔티티)
             *      update, delete 지원
             *      insert(insert into .. select, 하이버네이트 지원)
             *
             * 벌크 연산은 영속성 컨텍스트를 무시하고 데이터베이스에 직접 쿼리
             *      방법1: 벌크 연산을 먼저 실행
             *      방법2: 벌크 연산 수행 후 영속성 컨텍스트 초기화
             */
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();
            System.out.println("resultCount = " + resultCount);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}