package com.example.sadashivsinha.mprosmart.ModelLists;

/**
 * Created by saDashiv sinha on 04-Apr-16.
 */
public class NewAllProjectList
{
        private String project_name, project_id, project_desc, created_by, date, companyLogo, address_one, address_two, city,
                state, pincode, country , text_budget, text_currency, approved;

        public NewAllProjectList
            (String project_name, String project_id, String project_desc, String created_by,
             String date,String companyLogo, String address_one,String address_two,String city,
             String state,String pincode,String country ,String text_budget, String text_currency, String approved ) {

                        this.project_name = project_name;
                        this.project_id = project_id;
                        this.project_desc = project_desc;
                        this.created_by = created_by;
                        this.date = date;
                        this.companyLogo = companyLogo;
                        this.address_one = address_one;
                        this.address_two = address_two;
                        this.city = city;
                        this.state = state;
                        this.pincode = pincode;
                        this.country = country;
                        this.text_budget = text_budget;
                        this.text_currency = text_currency;
                        this.approved = approved;
    }

    public String getApproved() {
        return approved;
    }

    public String getText_budget() {
        return text_budget;
    }

    public String getText_currency() {
        return text_currency;
    }

    public String getAddress_one() {
        return address_one;
    }

    public String getAddress_two() {
        return address_two;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public String getCountry() {
        return country;
    }

    public String getProject_name() {
            return project_name;
        }

        public String getCompanyLogo() {
            return companyLogo;
        }

        public String getProject_id() {
            return project_id;
        }

        public String getProject_desc() {
            return project_desc;
        }

        public String getCreated_by() {
            return created_by;
        }

        public String getDate() {
            return date;
        }
}