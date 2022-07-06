package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain5 {

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
             * 경로 표현식
             *
             * 상태 필드(state field): 경로 탐색의 끝, 탐색X
             * 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색O
             * 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색X
             *      from 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
             *
             * select o.member.team from Order o (성공) - 단일 값 연관 경로
             * select t.members from Team (성공) - 컬렉션 값 연관 경로
             * select t.members.username from Team t (실패) - 경렉션 값 연관 경로
             * select m.username from Team t join t.members m (성공) - 컬렉션 값 연관 경로 -> 명시적 조인
             */
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