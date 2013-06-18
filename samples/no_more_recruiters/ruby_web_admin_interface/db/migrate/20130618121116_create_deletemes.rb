class CreateDeletemes < ActiveRecord::Migration
  def change
    create_table :deletemes do |t|
      t.string :title
      t.string :company
      t.string :description

      t.timestamps
    end
  end
end
