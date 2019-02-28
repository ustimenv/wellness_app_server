
public class Person
{
    private String name;
    private final long ID;   //id in database (primary key)
    private long passwordHash;
    int x = 10;
    public Person(String name, long passwordHash)
    {
       this.name = name;
       this.ID = x++;
       this.passwordHash = passwordHash;
    }

    public void setName(String newName)
    {
       name = newName;
    }
    
    public String getName()
    {
        return name;
    }

    public long getID()
    {
        return ID;
    }

}
