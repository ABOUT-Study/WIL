package designPattern.facade.homeTheater;

public class Facade {

    private String beverageName;
    private String movieName;

    public Facade(String beverage,String movieName)
    {
        this.beverageName=beverage;
        this.movieName=movieName;
    }

    public void view_Movie()
    {
        Beverage beverage = new Beverage(beverageName);
        RemoteControl remote= new RemoteControl();
        Movie movie = new Movie(movieName);

        beverage.Prepare();
        remote.Turn_On();
        movie.Search_Movie();
        movie.Charge_Movie();
        movie.play_Movie();
    }
}