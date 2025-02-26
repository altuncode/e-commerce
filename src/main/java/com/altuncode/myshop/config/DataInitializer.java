package com.altuncode.myshop.config;

import com.altuncode.myshop.model.*;
import com.altuncode.myshop.model.enums.ProductStatusEnum;
import com.altuncode.myshop.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("DataInitializer")
public class DataInitializer implements CommandLineRunner {
    private final ProductColorRepo productColorRepo;
    private final ProductCategoryRepo productCategoryRepo;
    private final ProductSubCategoryRepo productSubCategoryRepo;
    private final PersonRepo personRepo;
    private final CityRepo cityRepo;
    private final ProductRepo productRepo;

    @Autowired
    public DataInitializer(@Qualifier("ProductCategoryRepo") ProductCategoryRepo productCategoryRepo, @Qualifier("ProductColorRepo") ProductColorRepo productColorRepo, @Qualifier("ProductSubCategoryRepo") ProductSubCategoryRepo productSubCategoryRepo, @Qualifier("PersonRepo") PersonRepo personRepo, @Qualifier("CityRepo") CityRepo cityRepo, @Qualifier("ProductRepo") ProductRepo productRepo) {
        this.productCategoryRepo = productCategoryRepo;
        this.productColorRepo = productColorRepo;
        this.productSubCategoryRepo = productSubCategoryRepo;
        this.personRepo = personRepo;
        this.cityRepo = cityRepo;
        this.productRepo = productRepo;
    }


    @Override
    public void run(String... args) throws Exception {
        initializeCity();
        initializeProductColors();

    }

    private void initializeCity() {
        if (cityRepo.count() == 0) {
            City barrie = new City();
            barrie.setName("Barrie");
            barrie.setPrice(0.0);
            cityRepo.save(barrie);

            City belleville = new City();
            belleville.setName("Belleville");
            belleville.setPrice(0.0);
            cityRepo.save(belleville);

            City brampton = new City();
            brampton.setName("Brampton");
            brampton.setPrice(0.0);
            cityRepo.save(brampton);

            City brant = new City();
            brant.setName("Brant");
            brant.setPrice(0.0);
            cityRepo.save(brant);

            City brantford = new City();
            brantford.setName("Brantford");
            brantford.setPrice(0.0);
            cityRepo.save(brantford);

            City brockville = new City();
            brockville.setName("Brockville");
            brockville.setPrice(0.0);
            cityRepo.save(brockville);

            City burlington = new City();
            burlington.setName("Burlington");
            burlington.setPrice(0.0);
            cityRepo.save(burlington);

            City cambridge = new City();
            cambridge.setName("Cambridge");
            cambridge.setPrice(0.0);
            cityRepo.save(cambridge);

            City clarenceRockland = new City();
            clarenceRockland.setName("Clarence-Rockland");
            clarenceRockland.setPrice(0.0);
            cityRepo.save(clarenceRockland);

            City cornwall = new City();
            cornwall.setName("Cornwall");
            cornwall.setPrice(0.0);
            cityRepo.save(cornwall);

            City dryden = new City();
            dryden.setName("Dryden");
            dryden.setPrice(0.0);
            cityRepo.save(dryden);

            City elliotLake = new City();
            elliotLake.setName("Elliot Lake");
            elliotLake.setPrice(0.0);
            cityRepo.save(elliotLake);

            City greaterSudbury = new City();
            greaterSudbury.setName("Greater Sudbury");
            greaterSudbury.setPrice(0.0);
            cityRepo.save(greaterSudbury);

            City guelph = new City();
            guelph.setName("Guelph");
            guelph.setPrice(0.0);
            cityRepo.save(guelph);

            City haldimandCounty = new City();
            haldimandCounty.setName("Haldimand County");
            haldimandCounty.setPrice(0.0);
            cityRepo.save(haldimandCounty);

            City hamilton = new City();
            hamilton.setName("Hamilton");
            hamilton.setPrice(0.0);
            cityRepo.save(hamilton);

            City kawarthaLakes = new City();
            kawarthaLakes.setName("Kawartha Lakes");
            kawarthaLakes.setPrice(0.0);
            cityRepo.save(kawarthaLakes);

            City kenora = new City();
            kenora.setName("Kenora");
            kenora.setPrice(0.0);
            cityRepo.save(kenora);

            City kingston = new City();
            kingston.setName("Kingston");
            kingston.setPrice(0.0);
            cityRepo.save(kingston);

            City kitchener = new City();
            kitchener.setName("Kitchener");
            kitchener.setPrice(0.0);
            cityRepo.save(kitchener);

            City london = new City();
            london.setName("London");
            london.setPrice(0.0);
            cityRepo.save(london);

            City markham = new City();
            markham.setName("Markham");
            markham.setPrice(0.0);
            cityRepo.save(markham);

            City mississauga = new City();
            mississauga.setName("Mississauga");
            mississauga.setPrice(0.0);
            cityRepo.save(mississauga);

            City niagaraFalls = new City();
            niagaraFalls.setName("Niagara Falls");
            niagaraFalls.setPrice(0.0);
            cityRepo.save(niagaraFalls);

            City norfolkCounty = new City();
            norfolkCounty.setName("Norfolk County");
            norfolkCounty.setPrice(0.0);
            cityRepo.save(norfolkCounty);

            City northBay = new City();
            northBay.setName("North Bay");
            northBay.setPrice(0.0);
            cityRepo.save(northBay);

            City orillia = new City();
            orillia.setName("Orillia");
            orillia.setPrice(0.0);
            cityRepo.save(orillia);

            City oshawa = new City();
            oshawa.setName("Oshawa");
            oshawa.setPrice(0.0);
            cityRepo.save(oshawa);

            City ottawa = new City();
            ottawa.setName("Ottawa");
            ottawa.setPrice(0.0);
            cityRepo.save(ottawa);

            City owenSound = new City();
            owenSound.setName("Owen Sound");
            owenSound.setPrice(0.0);
            cityRepo.save(owenSound);

            City pembroke = new City();
            pembroke.setName("Pembroke");
            pembroke.setPrice(0.0);
            cityRepo.save(pembroke);

            City peterborough = new City();
            peterborough.setName("Peterborough");
            peterborough.setPrice(0.0);
            cityRepo.save(peterborough);

            City pickering = new City();
            pickering.setName("Pickering");
            pickering.setPrice(0.0);
            cityRepo.save(pickering);

            City portColborne = new City();
            portColborne.setName("Port Colborne");
            portColborne.setPrice(0.0);
            cityRepo.save(portColborne);

            City princeEdwardCounty = new City();
            princeEdwardCounty.setName("Prince Edward County");
            princeEdwardCounty.setPrice(0.0);
            cityRepo.save(princeEdwardCounty);

            City quinteWest = new City();
            quinteWest.setName("Quinte West");
            quinteWest.setPrice(0.0);
            cityRepo.save(quinteWest);

            City richmondHill = new City();
            richmondHill.setName("Richmond Hill");
            richmondHill.setPrice(0.0);
            cityRepo.save(richmondHill);

            City sarnia = new City();
            sarnia.setName("Sarnia");
            sarnia.setPrice(0.0);
            cityRepo.save(sarnia);

            City saultSteMarie = new City();
            saultSteMarie.setName("Sault Ste. Marie");
            saultSteMarie.setPrice(0.0);
            cityRepo.save(saultSteMarie);

            City stCatharines = new City();
            stCatharines.setName("St. Catharines");
            stCatharines.setPrice(0.0);
            cityRepo.save(stCatharines);

            City stThomas = new City();
            stThomas.setName("St. Thomas");
            stThomas.setPrice(0.0);
            cityRepo.save(stThomas);

            City stratford = new City();
            stratford.setName("Stratford");
            stratford.setPrice(0.0);
            cityRepo.save(stratford);

            City temiskamingShores = new City();
            temiskamingShores.setName("Temiskaming Shores");
            temiskamingShores.setPrice(0.0);
            cityRepo.save(temiskamingShores);

            City thorold = new City();
            thorold.setName("Thorold");
            thorold.setPrice(0.0);
            cityRepo.save(thorold);

            City thunderBay = new City();
            thunderBay.setName("Thunder Bay");
            thunderBay.setPrice(0.0);
            cityRepo.save(thunderBay);

            City timmins = new City();
            timmins.setName("Timmins");
            timmins.setPrice(0.0);
            cityRepo.save(timmins);

            City toronto = new City();
            toronto.setName("Toronto");
            toronto.setPrice(0.0);
            cityRepo.save(toronto);

            City vaughan = new City();
            vaughan.setName("Vaughan");
            vaughan.setPrice(0.0);
            cityRepo.save(vaughan);

            City waterloo = new City();
            waterloo.setName("Waterloo");
            waterloo.setPrice(0.0);
            cityRepo.save(waterloo);

            City welland = new City();
            welland.setName("Welland");
            welland.setPrice(0.0);
            cityRepo.save(welland);

            City windsor = new City();
            windsor.setName("Windsor");
            windsor.setPrice(0.0);
            cityRepo.save(windsor);

            City woodstock = new City();
            woodstock.setName("Woodstock");
            woodstock.setPrice(0.0);
            cityRepo.save(woodstock);
        }
    }

    //Altun buna baxdi
    private void initializeProductColors() {
        if (productColorRepo.count() == 0) {
            ProductColor softCreamWhite = new ProductColor();
            softCreamWhite.setName("White High Gloss");
            softCreamWhite.setHexCode("#FFFFFF");
            softCreamWhite.setOrderNumber(1);
            productColorRepo.save(softCreamWhite);
        }
    }





}

