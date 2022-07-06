package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain8 {

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
             * 엔티티 직접 사용 - 직접 객체를 넘기면 자동으로 id를 식별자로 변환시켜준다.
             *      기본 키 값
             *      외래 키 값
             */
            //기본 키 값
            em.createQuery("select count(m) from Member m", Long.class).getResultList();
            em.createQuery("select m from Member m where m = :member", Member.class)
                    .setParameter("member", member1)
                    .getResultList();

            //외래 키 값
            em.createQuery("select m from Member m where m.team = :team", Member.class)
                    .setParameter("team", teamA)
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