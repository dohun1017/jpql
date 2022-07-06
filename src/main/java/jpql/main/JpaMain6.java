package jpql.main;

import jpql.Member;
import jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain6 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            /**
             * 페치 조인 (SQL 문법 X) - 지연 로딩이 아닌 즉시 로딩
             *      페치 조인 실행시 연관된 엔티티를 함께 조회한다. (O)
             *      일반 조인 실행시 연관된 엔티티를 함게 조회하지 않는다. (X)
             *
             * 엔티티 조인 => N + 1 문제 발생
             * 회원1, 팀A(SQL)
             * 회원2, 팀A(1차 캐시)
             * 회원3, 팀B(SQL)
             *
             * 엔티티 페치 조인
             * 처음부터 Member, Team 엔티티를 전부 가져온다.(프록시가 아님)
             *
             * @OneToMany(일대다) -> 결과 줄수가 부풀려서 나올 수 있다.
             * JPQL의 distinct 2가지 기능 제공
             *      1. SQL에 distinct 추가
             *          완전히 같아야만 중복이 삭제됨.
             *      2. 애플리케이션에서 엔티티 중복 제거
             *          같은 식별자를 가진 Team 엔티티 제거.
             *
             */
            //@ManyToOne(다대일) 조인
            em.createQuery("select m from Member m join fetch m.team", Member.class)
                    .getResultList()
                            .forEach(m -> {
                                System.out.println("m.getUsername() = " + m.getUsername());
                                System.out.println("m.getTeam().getName() = " + m.getTeam().getName());
                            });
            /**
             * @OneToMany(일대다) 조인
             *
             * distinct 추가시
             *      1. SQL (3 -> 3)
             *      2. 엔티티 중복 제거 (3 -> 2)
             */
            em.createQuery("select distinct t from Team t join fetch t.members", Team.class)
                    .getResultList()
                    .forEach(t -> {
                        List<Member> members = t.getMembers();
                        for (Member member : members) {
                            System.out.println("member.getUsername() = " + member.getUsername());
                        }
                        System.out.println("t.getName() = " + t.getName());
                    });

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