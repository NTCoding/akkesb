class DeletemesController < ApplicationController
  # GET /deletemes
  # GET /deletemes.json
  def index
    @deletemes = Deleteme.all

    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @deletemes }
    end
  end

  # GET /deletemes/1
  # GET /deletemes/1.json
  def show
    @deleteme = Deleteme.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @deleteme }
    end
  end

  # GET /deletemes/new
  # GET /deletemes/new.json
  def new
    @deleteme = Deleteme.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @deleteme }
    end
  end

  # GET /deletemes/1/edit
  def edit
    @deleteme = Deleteme.find(params[:id])
  end

  # POST /deletemes
  # POST /deletemes.json
  def create
    @deleteme = Deleteme.new(params[:deleteme])

    respond_to do |format|
      if @deleteme.save
        format.html { redirect_to @deleteme, notice: 'Deleteme was successfully created.' }
        format.json { render json: @deleteme, status: :created, location: @deleteme }
      else
        format.html { render action: "new" }
        format.json { render json: @deleteme.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /deletemes/1
  # PUT /deletemes/1.json
  def update
    @deleteme = Deleteme.find(params[:id])

    respond_to do |format|
      if @deleteme.update_attributes(params[:deleteme])
        format.html { redirect_to @deleteme, notice: 'Deleteme was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @deleteme.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /deletemes/1
  # DELETE /deletemes/1.json
  def destroy
    @deleteme = Deleteme.find(params[:id])
    @deleteme.destroy

    respond_to do |format|
      format.html { redirect_to deletemes_url }
      format.json { head :no_content }
    end
  end
end
