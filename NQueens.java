import java.util.*;

public class NQueens{
    
    
    public static void main(String[] args){
        //accept in 3 parameters:
        // size, ps, pss
        // size is the N in n queens look it up
        // ps is the size of the population
        // pss is the parent sample size
        int size = Integer.parseInt(args[0]);
        int ps = Integer.parseInt(args[1]);
        int pss = Integer.parseInt(args[2]);
        //construct our population array list
        ArrayList<QObject> population = popBuilder(ps, size);
        //setup the qfinder 
        QFinder qf = new QFinder(population, ps, pss);
        //System.out.println(qf.population);
        //run the qfinder
        qf.run();

    }
    
    //randomizer mixes up the array inorder to have a random population
    public static void randomizer(int[] target){
        Random rand = new Random();
        //swap things randomly
        for(int i = 0; i<target.length *2; i++){
            //grab random positions in the array
            int a = rand.nextInt(target.length);
            int b = rand.nextInt(target.length);
            
            //grab the values from each position
            int first = target[a];
            int second = target[b];
            
            //swap the values
            target[a] = second;
            target[b] = first;
        }
    }
    
    //this just creates the inital populatiion
    public static ArrayList<QObject> popBuilder(int ps, int size){
        ArrayList<QObject> result = new ArrayList<>();
        for(int i = 1; i <= ps; i++){
            int [] temp = genotypeBuilder(size);
            result.add(new QObject(temp, fitness(temp)));
        }
        return result;
    }
    
    //creates the single individual and randomizes it
    public static int[] genotypeBuilder(int size){
        int[] result = new int[size];
        for(int i = 0; i < size; i ++){
            result[i] = i + 1;
        }
        randomizer(result);
        return result;
    }
    
    //this is the fitness function it tells us how good our thingy does
    public static int fitness(int[] a){
        int collisions = 0;
        for(int i = 0; i < a.length; i ++){
            //check the first half
            for(int j = 0 ; j <= i - 1 ; j++){
                
                if(a[i] + (i-j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            for(int j = i+1; j < a.length; j++){
                if(a[i] + (i- j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            
        }
        
        return collisions;
    }
}


//q object is an individual specimen
//it holds the fitness score and the genotype
class QObject{
    int fitness;
    int[] genotype;
    int age;
    public QObject(int[] a, int fitness){
        this.fitness = fitness;
        genotype = a;
        age = 0;
    }
    
    public int fitness(){
        return fitness;
    }
    
    public int[] genotype(){
        return genotype;
    }
    
    @Override
    public String toString(){
        return fitness + " " + Arrays.toString(genotype);
    }
    
}

//qfinder is the meat of the algorithm this does the work
//creates and find the solution to a NQueen board
class QFinder{
    
    ArrayList<QObject> population;
    int size;
    int numberOfParents;
    
    public QFinder(ArrayList<QObject> a, int s, int parents){
        population = a;
        size = s;
        numberOfParents = parents;
    }
    
    public void run(){
        
        boolean s = true;
        int c = 0;
        while(s){
            //sort arraylist by fitness
            this.fitnessSort();
            //easy enough  to find a winning solution
            if(population.get(0).fitness() == 0){
                System.out.println("FOUND A MATCH");
                System.out.println(population.get(0));
                System.exit(0);
            }   
            //update the age of all subjects
            for(int i = 0; i < population.size(); i++){
                population.get(i).age++;
                if(population.get(i).age > 1000)
                    population.remove(i);
            }
            
            
            //kill of any items that do not meet the fitness cut off
            //only keep the initial population size amount
            while(population.size() > this.size){
                //remove things until we have the size
                population.remove(population.size()-1);
            }
            
           
            //keeps track of how many times we loop
            c++;
            System.out.print(population.get(0).fitness() + " ");
            System.out.print(population.size() + " ");
            System.out.println(c);
            
            //find parents
            QObject[] parents = new QObject[numberOfParents];
            int[] picks = new int[numberOfParents];
            
            //patent pending parent finding algorithm this alforithm pits 2
            //solutiosn against each other, the higher one wins, if there is a 
            // tie a always wins
            
            //a and b are randomly selected
            for(int i = 0; i < numberOfParents; i++){
                //doh, just make the array full of -1's
                for(int j = 0; j < picks.length; j ++)
                    picks[i]--;
                
                //wooo random pickings (LOTTERY TIME
                Random rand = new Random();
                int a = rand.nextInt(this.size);
                int b = rand.nextInt(this.size);
                
                //make sure the individual isn't inside the array already
                for(int j = 0; j < picks.length; j++){
                    while(picks[j] == a)
                        a = rand.nextInt(this.size);
                    while(picks[j] == b)
                        b = rand.nextInt(this.size);
                }
                
                //fitness fighting
                if( a >= b)
                    picks[i] = a;
                else
                    picks[i] = b;
            }
            
            //once we have the parents we can do recombination
            for(int i = 1; i < picks.length; i = i + 2){
                combine(population.get(picks[i-1]),population.get( picks[i]));
            }
        }
        

    }
    
    
    //the combination function woooo
    private void combine(QObject a, QObject b){
        //pick a combination point
        Random r = new Random();
        int cp = r.nextInt(a.genotype().length - 2) + 1;
        
        
        //create the new individuals
        int[] baby1 = new int[a.genotype().length];
        int[] baby2 = new int[a.genotype().length];
        for(int i = 0; i < cp; i++){
            baby1[i] = a.genotype()[i];
            baby2[i] = b.genotype()[i];
        }
        
        //from the cp point go through each element in the other array and check 
        //if the baby contains it already if not add it in at the cp point
        int newCP = cp;
        while(newCP != baby1.length){
            for(int i = 0; i < b.genotype.length; i++){
                if(!contains(baby1, b.genotype[i])){
                    baby1[newCP] = b.genotype[i];
                    newCP++;
                }
            }
        }
        newCP = cp;
        while(newCP != baby2.length){
            for(int i = 0; i < a.genotype.length; i++){
                if(!contains(baby2, a.genotype[i])){
                    baby2[newCP] = a.genotype[i];
                    newCP++;
                }
            }
        }

        //System.out.println(Arrays.toString(baby1));
        //random mutation
        mutate(baby1);
        mutate(baby2);
        population.add(new QObject(baby1, fitness(baby1)));
        population.add(new QObject(baby1, fitness(baby2)));
    }
    
    private boolean contains(int[] a, int value){
        for(int i = 0; i < a.length; i++){
            if(a[i] == value)
                return true;
        }
        
        return false;
    }
    
    public void mutate(int[] a){
        Random r = new Random();
        int b = r.nextInt(a.length);
        int c = r.nextInt(a.length);
        if(r.nextInt(100) > 50){
            int temp = a[b];
            a[b] = a[c];
            a[c] = temp;
            
        }
        
    }
    
    private void fitnessSort(){
        QObject[] a = new QObject[this.population.size()];
        QObject insert = this.population.remove(population.size()-1);
        a[0] = insert;
        int aSize = 1;
        while(!population.isEmpty()){
            insert = this.population.remove(population.size()-1);


            int insertPos = aSize;

            for(int i = 0; i < aSize; i++){
                if(insert.fitness() < a[i].fitness()){
                    insertPos = i;
                    break;
                }
            }
            if(insertPos == aSize){
                a[insertPos] = insert;
                aSize++;
                
            } else {
                insert(a, insert, insertPos, aSize);
                aSize++;
            }
        }
       for(int i = 0; i < a.length; i++)
            this.population.add(a[i]);
        
    }
    private void insert(QObject[] a, QObject b, int pos, int aSize){
        for(int i = aSize; i > pos ; i --){
            a[i]= a[i - 1];
        }
        a[pos ] = b;
    }
    
    public int fitness(int[] a){
        int collisions = 0;
        for(int i = 0; i < a.length; i ++){
            //check the first half
            for(int j = 0 ; j <= i - 1 ; j++){
                
                if(a[i] + (i-j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            for(int j = i+1; j < a.length; j++){
                if(a[i] + (i- j) == a[j])
                    collisions++;
                if(a[i] - (i -j) == a[j])
                    collisions++;
            }
            
        }
        
        return collisions;
    }
}