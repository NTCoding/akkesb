class JobListingsController < ApplicationController
  # GET /job_listings
  # GET /job_listings.json
  def index
    @job_listings = JobListing.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @job_listings }
    end
  end

  # GET /job_listings/1
  # GET /job_listings/1.json
  def show
    @job_listing = JobListing.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @job_listing }
    end
  end

  # GET /job_listings/new
  # GET /job_listings/new.json
  def new
    @job_listing = JobListing.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @job_listing }
    end
  end

  # GET /job_listings/1/edit
  def edit
    @job_listing = JobListing.find(params[:id])
  end

  # POST /job_listings
  # POST /job_listings.json
  def create
    @job_listing = JobListing.new(params[:job_listing])

    respond_to do |format|
      if @job_listing.save
        format.html { redirect_to @job_listing, notice: 'Job listing was successfully created.' }
        format.json { render json: @job_listing, status: :created, location: @job_listing }
      else
        format.html { render action: "new" }
        format.json { render json: @job_listing.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /job_listings/1
  # PUT /job_listings/1.json
  def update
    @job_listing = JobListing.find(params[:id])

    respond_to do |format|
      if @job_listing.update_attributes(params[:job_listing])
        format.html { redirect_to @job_listing, notice: 'Job listing was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @job_listing.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /job_listings/1
  # DELETE /job_listings/1.json
  def destroy
    @job_listing = JobListing.find(params[:id])
    @job_listing.destroy

    respond_to do |format|
      format.html { redirect_to job_listings_url }
      format.json { head :no_content }
    end
  end
end
