/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.model;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "daily_pnr_counts")
public class PnrStatistics {

     private static final long serialVersionUID = 1L;
     public PnrStatistics() { }
    
        @Id  
        @GeneratedValue(strategy = GenerationType.TABLE)  
        @Basic(optional = false)  
        @Column(name = "id", nullable = false, columnDefinition = "bigint unsigned")  
        private Long id;  
        
        @Column(name = "day", nullable = false)
        @Temporal(TemporalType.DATE)
        private Date today;
        
        @Column(name = "1am", nullable = false)
        private Integer one = Integer.valueOf(0);
        
        @Column(name = "2am", nullable = false)
        private Integer two = Integer.valueOf(0);
        
        @Column(name = "3am", nullable = false)
        private Integer three = Integer.valueOf(0);
        
        @Column(name = "4am", nullable = false)
        private Integer four = Integer.valueOf(0);
        
        @Column(name = "5am", nullable = false)
        private Integer five = Integer.valueOf(0);
        
        @Column(name = "6am", nullable = false)
        private Integer six = Integer.valueOf(0);
        
        @Column(name = "7am", nullable = false)
        private Integer seven = Integer.valueOf(0);
        
        @Column(name = "8am", nullable = false)
        private Integer eight = Integer.valueOf(0);
        
        @Column(name = "9am", nullable = false)
        private Integer nine = Integer.valueOf(0);
        
        @Column(name = "10am", nullable = false)
        private Integer ten = Integer.valueOf(0);
        
        @Column(name = "11am", nullable = false)
        private Integer eleven = Integer.valueOf(0);
        
        @Column(name = "12pm", nullable = false)
        private Integer twelve = Integer.valueOf(0);
        
        @Column(name = "1pm", nullable = false)
        private Integer thirteen = Integer.valueOf(0);
        
        @Column(name = "2pm", nullable = false)
        private Integer fourteen = Integer.valueOf(0);
        
        @Column(name = "3pm", nullable = false)
        private Integer fifteen = Integer.valueOf(0);
        
        @Column(name = "4pm", nullable = false)
        private Integer sixteen = Integer.valueOf(0);
        
        @Column(name = "5pm", nullable = false)
        private Integer seventeen = Integer.valueOf(0);
        
        @Column(name = "6pm", nullable = false)
        private Integer eighteen = Integer.valueOf(0);
        
        @Column(name = "7pm", nullable = false)
        private Integer nineteen = Integer.valueOf(0);
        
        @Column(name = "8pm", nullable = false)
        private Integer twenty = Integer.valueOf(0);
        
        @Column(name = "9pm", nullable = false)
        private Integer twentyOne = Integer.valueOf(0);
        
        @Column(name = "10pm", nullable = false)
        private Integer twentyTwo = Integer.valueOf(0);
        
        @Column(name = "11pm", nullable = false)
        private Integer twentyThree = Integer.valueOf(0);
        
        @Column(name = "12am", nullable = false)
        private Integer zero = Integer.valueOf(0);
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getToday() {
            return today;
        }

        public void setToday(Date today) {
            this.today = today;
        }

        public Integer getOne() {
            return one;
        }

        public void setOne(Integer one) {
            this.one = one;
        }

        public Integer getTwo() {
            return two;
        }

        public void setTwo(Integer two) {
            this.two = two;
        }

        public Integer getThree() {
            return three;
        }

        public void setThree(Integer three) {
            this.three = three;
        }

        public Integer getFour() {
            return four;
        }

        public void setFour(Integer four) {
            this.four = four;
        }

        public Integer getFive() {
            return five;
        }

        public void setFive(Integer five) {
            this.five = five;
        }

        public Integer getSix() {
            return six;
        }

        public void setSix(Integer six) {
            this.six = six;
        }

        public Integer getSeven() {
            return seven;
        }

        public void setSeven(Integer seven) {
            this.seven = seven;
        }

        public Integer getEight() {
            return eight;
        }

        public void setEight(Integer eight) {
            this.eight = eight;
        }

        public Integer getNine() {
            return nine;
        }

        public void setNine(Integer nine) {
            this.nine = nine;
        }

        public Integer getTen() {
            return ten;
        }

        public void setTen(Integer ten) {
            this.ten = ten;
        }

        public Integer getEleven() {
            return eleven;
        }

        public void setEleven(Integer eleven) {
            this.eleven = eleven;
        }

        public Integer getTwelve() {
            return twelve;
        }

        public void setTwelve(Integer twelve) {
            this.twelve = twelve;
        }

        public Integer getThirteen() {
            return thirteen;
        }

        public void setThirteen(Integer thirteen) {
            this.thirteen = thirteen;
        }

        public Integer getFourteen() {
            return fourteen;
        }

        public void setFourteen(Integer fourteen) {
            this.fourteen = fourteen;
        }

        public Integer getFifteen() {
            return fifteen;
        }

        public void setFifteen(Integer fifteen) {
            this.fifteen = fifteen;
        }

        public Integer getSixteen() {
            return sixteen;
        }

        public void setSixteen(Integer sixteen) {
            this.sixteen = sixteen;
        }

        public Integer getSeventeen() {
            return seventeen;
        }

        public void setSeventeen(Integer seventeen) {
            this.seventeen = seventeen;
        }

        public Integer getEighteen() {
            return eighteen;
        }

        public void setEighteen(Integer eighteen) {
            this.eighteen = eighteen;
        }

        public Integer getNineteen() {
            return nineteen;
        }

        public void setNineteen(Integer nineteen) {
            this.nineteen = nineteen;
        }

        public Integer getTwenty() {
            return twenty;
        }

        public void setTwenty(Integer twenty) {
            this.twenty = twenty;
        }

        public Integer getTwentyOne() {
            return twentyOne;
        }

        public void setTwentyOne(Integer twentyOne) {
            this.twentyOne = twentyOne;
        }

        public Integer getTwentyTwo() {
            return twentyTwo;
        }

        public void setTwentyTwo(Integer twentyTwo) {
            this.twentyTwo = twentyTwo;
        }

        public Integer getTwentyThree() {
            return twentyThree;
        }

        public void setTwentyThree(Integer twentyThree) {
            this.twentyThree = twentyThree;
        }

        public Integer getZero() {
            return zero;
        }

        public void setZero(Integer zero) {
            this.zero = zero;
        }
        
        public String toString(){
            return "Today : "+this.today+" counts are : "+this.one+this.two+this.three+this.four+this.five+this.six+this.seven+this.eight+this.nine+this.ten+this.eleven+this.twelve;
            
        }
}
