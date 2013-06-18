class JobListingsController < ApplicationController
  # GET /job_listings
  # GET /job_listings.json
  #def index
  #  @job_listings = JobListing.all

   # respond_to do |format|
    #  format.html # index.html.erb
     # format.json { render json: @job_listings }
   # end
  #end

  # GET /job_listings/1
  # GET /job_listings/1.json
  #def show
   # @job_listing = JobListing.find(params[:id])

    #respond_to do |format|
     # format.html # show.html.erb
      #format.json { render json: @job_listing }
   # end
 # end

  # GET /job_listings/new
  # GET /job_listings/new.json
  def new
    @job_listing = JobListing.new
  end

end
