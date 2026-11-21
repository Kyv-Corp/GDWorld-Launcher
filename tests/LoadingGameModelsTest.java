package pro.gravit.launcher.gui.components;

import java.util.*;

public class LoadingGameModelsTest {
    static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    public static void main(String[] args){
        Random r=new Random(14);int previous=-1;Set<Integer> seen=new HashSet<>();
        for(int i=0;i<100;i++){int next=LoadingGameModels.chooseDifferent(r,previous,3);check(next!=previous,"Consecutive game repeated");seen.add(next);previous=next;}
        check(seen.size()==3,"Game selection never reached all games");
        var s=new LoadingGameModels.Snake(16,new Random(4));s.food=new LoadingGameModels.Cell(6,8);s.turn(-1,0);s.step();
        check(s.score==1&&s.body.size()==5&&s.body.getFirst().x()==6,"Snake growth or reversal guard failed");
        check(!s.body.contains(s.food),"Food inside snake");
        for(int i=0;i<20;i++)s.step();check(s.over,"Wall collision was ignored");
        s=new LoadingGameModels.Snake(16,new Random(1));s.turn(0,-1);s.turn(-1,0);s.step();check(s.body.getFirst().equals(new LoadingGameModels.Cell(5,7)),"Two rapid turns reversed snake");
        s.body.clear();s.body.addAll(List.of(new LoadingGameModels.Cell(2,2),new LoadingGameModels.Cell(2,3),new LoadingGameModels.Cell(1,3),new LoadingGameModels.Cell(1,2)));s.dx=0;s.dy=-1;s.nextDx=-1;s.nextDy=0;s.food=new LoadingGameModels.Cell(10,10);s.step();check(!s.over,"Moving into vacating tail should be legal");
        for(int seed=0;seed<40;seed++){
            var m=new LoadingGameModels.Mines(9,10,new Random(seed));m.flag(0,0);m.reveal(0,0);check(!m.generated,"Flagged first square opened");m.flag(0,0);m.reveal(4,4);
            int bombs=0;for(int y=0;y<9;y++)for(int x=0;x<9;x++){if(m.mines[y][x])bombs++;if(Math.abs(x-4)<=1&&Math.abs(y-4)<=1)check(!m.mines[y][x],"First-click neighborhood unsafe");}
            check(bombs==10&&!m.over&&m.openCount>=9,"Mine generation/flood fill failed");
            for(int y=0;y<9;y++)for(int x=0;x<9;x++)if(!m.mines[y][x])m.reveal(x,y);
            check(m.won&&m.openCount==71,"Mines victory failed");
        }
        var lost=new LoadingGameModels.Mines(9,10,new Random(2));lost.reveal(4,4);
        outer:for(int y=0;y<9;y++)for(int x=0;x<9;x++)if(lost.mines[y][x]){lost.reveal(x,y);break outer;}
        check(lost.over&&!lost.won,"Mine explosion failed");
        var memory=new LoadingGameModels.Memory(new Random(7));Map<Integer,List<Integer>> pairs=new HashMap<>();
        for(int i=0;i<12;i++)pairs.computeIfAbsent(memory.cards[i],k->new ArrayList<>()).add(i);
        check(pairs.size()==6&&pairs.values().stream().allMatch(v->v.size()==2),"Invalid memory deck");
        int other=1;while(memory.cards[other]==memory.cards[0])other++;
        memory.reveal(0);memory.reveal(0);check(memory.moves==0,"Same card counted twice");memory.reveal(other);check(memory.moves==1,"Mismatch attempt missing");
        memory.reveal(11);check(memory.second==other,"Third card opened during mismatch");check(!memory.tick(699),"Mismatch hidden too early");check(memory.tick(1)&&memory.first==-1,"Mismatch did not hide");
        for(var p:pairs.values()){memory.reveal(p.get(0));memory.reveal(p.get(1));}
        check(memory.won()&&memory.moves==7,"Memory match/victory failed");
        System.out.println("PASS: non-repeating selection, snake rules, 40 safe mine fields/flood fills/wins, mine loss, memory pairs/mismatch lock/win");
    }
}
