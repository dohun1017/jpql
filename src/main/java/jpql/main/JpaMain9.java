package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain9 {

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
             * Named 쿼리 - 정적 쿼리
             *      애플리케이션 로딩 시점에 쿼리를 검증한다.
             *      애플리케이션 로딩 시점에 초기화 후 사용한다.
             *      어노테이션 또는 XML에 정의한다.
             *
             *      관례상 엔티티명.쿼리명(Member.findByUsername)
             */
            em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", member1.getUsername())
                    .getResultList();

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