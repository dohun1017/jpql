package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain4 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            /**
             * 내부 조인("select m from Member m [inner] join m.team t") inner 생략 가능
             * 외부 조인("select m from Member m left [outer] join m.team t") outer 생략 가능
             * 세타 조인("select count(m) from Member m, Team t where m.username = t.name")
             */
            //내부 조인
            em.createQuery("select m from Member m inner join m.team t", Member.class).getResultList();
            //외부 조인
            em.createQuery("select m from Member m left join m.team t", Member.class).getResultList();
            //세타 조인
            em.createQuery("select m from Member m, Team t where m.username = t.name", Member.class).getResultList();

            /**
             * ON 절 사용
             * 1. 조인 대상 필터링
             * 2. 연관관계 없는 엔티티 외부 조인
             */
            //조인 대상 필터링
            em.createQuery("select m from Member m left join m.team t on t.name='teamA'", Member.class).getResultList();
            //연관관계 없는 엔티티 외부 조인
            em.createQuery("select m from Member m join Team t on m.username = t.name", Member.class).getResultList();

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