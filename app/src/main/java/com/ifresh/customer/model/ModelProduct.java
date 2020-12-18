package com.ifresh.customer.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class ModelProduct implements Serializable , Comparable<ModelProduct>   {

    String productId, title, catId, frProductId, franchiseId, description, product_img, product_img_id;
    Boolean isPacket;
    private ArrayList<ModelProductVariation> priceVariations;
    public double globalStock;

    public ArrayList<ModelProductVariation> getPriceVariations() {
        return priceVariations;
    }

    public void setPriceVariations(ArrayList<ModelProductVariation> priceVariations) {
        this.priceVariations = priceVariations;
    }

    public double getGlobalStock() {
        return globalStock;
    }

    public void setGlobalStock(double globalStock) {
        this.globalStock = globalStock;
    }

    public String getProduct_img_id() {
        return product_img_id;
    }

    public void setProduct_img_id(String product_img_id) {
        this.product_img_id = product_img_id;
    }

    public Boolean getPacket() {
        return isPacket;
    }

    public void setPacket(Boolean packet) {
        isPacket = packet;
    }

    public String getId() {
        return productId;
    }

    public void setId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return title;
    }

    public void setName(String title) {
        this.title = title;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getFrProductId() {
        return frProductId;
    }

    public void setFrProductId(String frProductId) {
        this.frProductId = frProductId;
    }

    public String getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(String franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }


    @Override
    public int compareTo(ModelProduct o) {
        try {
            return this.getPriceVariations().get(0).getPrice().compareTo(o.getPriceVariations().get(0).getPrice());

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    public static Comparator<ModelProduct> compareByATOZ = new Comparator<ModelProduct>() {
        @Override
        public int compare(ModelProduct o1, ModelProduct o2) {
            return o1.getName().toUpperCase().compareToIgnoreCase(o2.getName().toUpperCase());
        }
    };

    public static Comparator<ModelProduct> compareByZTOA = new Comparator<ModelProduct>() {
        @Override
        public int compare(ModelProduct o1, ModelProduct o2) {
            return o2.getName().toUpperCase().compareToIgnoreCase(o1.getName().toUpperCase());
        }
    };

    public static Comparator<ModelProduct> compareByPriceVariations = new Comparator<ModelProduct>()
    {
        @Override
        public int compare(ModelProduct o1, ModelProduct o2) {
            int price1=0,price2=0;
            try{
                price1 =  Integer.parseInt(o1.getPriceVariations().get(0).getPrice());
                price2 =  Integer.parseInt(o2.getPriceVariations().get(0).getPrice());
                //Log.d("price1", ""+price1);
                //Log.d("price2", ""+price2);
                return price1-price2;
              }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return 0;
            //return o1.getPriceVariations().get(0).getPrice().compareTo(o2.getPriceVariations().get(0).getPrice());
        }
    };



    public static Comparator<ModelProduct> compareByPriceVariations_1 = new Comparator<ModelProduct>()
    {
        @Override
        public int compare(ModelProduct o1, ModelProduct o2) {
            int price1=0,price2=0;
            try{
                price1 =  Integer.parseInt(o1.getPriceVariations().get(0).getPrice());
                price2 =  Integer.parseInt(o2.getPriceVariations().get(0).getPrice());
                //Log.d("price1", ""+price1);
                //Log.d("price2", ""+price2);
                return price2-price1;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return 0;
            //return o1.getPriceVariations().get(0).getPrice().compareTo(o2.getPriceVariations().get(0).getPrice());
        }
    };





}
