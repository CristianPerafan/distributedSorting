public class ComparableClass implements Comparable<ComparableClass>{
    private String data;

    public String getData() {
        return data;
    }

    public ComparableClass(String data){
        this.data = data;
    }

    @Override
    public int compareTo(ComparableClass o) {
        return this.data.compareTo(o.data);
    }

    @Override
    public String toString(){
        return data;
    }
}
