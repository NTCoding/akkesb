class JobListing 
	extend ActiveModel::Naming
  	include ActiveModel::Conversion
	attr_accessor :company, :description, :title
  	
  	def persisted?
    	false
  	end
  
end
