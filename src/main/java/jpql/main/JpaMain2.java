package jpql.main;

import jpql.Address;
import jpql.Member;
import jpql.MemberDTO;
import jpql.Team;

import javax.persistence.*;
import java.util.List;

public class JpaMain2 {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            /**
             * 엔티티 타입 프로젝션 결과는 모두 영속성 컨텍스트이다.
             */
            em.createQuery("select m from Member as m", Member.class)
                    .getResultList().forEach(m -> {
                        System.out.println("member.username() = " + m.getUsername());
                        m.setUsername("newMember");
                    });

            /**
             * 묵시적 조인
             *      em.createQuery("select m.team from Member as m", Team.class)
             * 명시적 조인(더 추천)
             *      em.createQuery("select t from Member as m join m.team as t", Team.class)
             */
            em.createQuery("select m.team from Member as m", Team.class);

            /**
             * 임베디드 타입 프로젝션
             *      em.createQuery("select m.team from Member as m", Team.class)
             */
            em.createQuery("select o.address from Order as o", Address.class);


            /**
             * 스칼라 타입 프로젝션
             */
            //Query 타입으로 조회
            List resultList1 = em.createQuery("select distinct m.username, m.age from Member as m").getResultList();
            for (Object o : resultList1) {
                Object[] result = (Object[]) o;
                System.out.println("username = " + result[0]);
                System.out.println("age = " + result[1]);
            }
            //Object[] 타입으로조회
            List<Object[]> resultList2 = em.createQuery("select distinct m.username, m.age from Member as m").getResultList();
            for (Object[] objects : resultList2) {
                System.out.println("username = " + objects[0]);
                System.out.println("age = " + objects[1]);
            }
            //new 명령어로 조회(패키지 명을 적어줘야함)
            List<MemberDTO> resultList3 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member as m", MemberDTO.class)
                    .getResultList();
            for (MemberDTO memberDTO : resultList3) {
                System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
                System.out.println("memberDTO.getAge() = " + memberDTO.getAge());
            }


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