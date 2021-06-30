CREATE TABLE public.customer_information (
	customer_id uuid NOT NULL,
	customer_name varchar NOT NULL,
	customer_date_of_birth timestamptz(0) NOT NULL,
	customer_address varchar NOT NULL,
	CONSTRAINT customer_information_pk PRIMARY KEY (customer_id)
);



CREATE TABLE public.bank_branches (
	branch_name varchar(255) NOT NULL,
	branch_city varchar(255) NOT NULL,
	branch_created_date timestamptz(0) NOT NULL,
	branch_id serial NOT NULL,
	CONSTRAINT bank_branches_pk PRIMARY KEY (branch_id),
	CONSTRAINT bank_branches_un UNIQUE (branch_name)
);

insert into bank_branches (branch_id,branch_name,branch_city,branch_created_date)
VALUES 
(1,'Bank Rak','Bankgkok',Current_Timestamp),
(2,'Silom','Bankgkok',Current_Timestamp),
(3,'Asok','Bankgkok',Current_Timestamp),
(4,'Bang Lamung','Chonburi',Current_Timestamp),
(5,'Pattaya Beach','Chonburi',Current_Timestamp),
(6,'Nong Mon','Chonburi',Current_Timestamp),
(7,'Pa Tong','Phuket',Current_Timestamp),
(8,'Phuket International Airport','Phuket',Current_Timestamp),
(9,'Mae Jo','Chiang Mai',Current_Timestamp),
(10,'Mae Rim','Chiang Mai',Current_Timestamp)


CREATE TABLE public.bank_accounts (
	account_id uuid NOT NULL,
	account_number varchar NOT NULL,
	account_name varchar(255) NOT NULL,
	account_balance numeric NOT NULL,
	account_status varchar(255) NOT NULL,
	account_created_date timestamptz(0) NOT NULL,
	account_updated_date timestamptz(0) NOT NULL,
	account_branch_id serial NOT NULL,
	CONSTRAINT bank_accounts_pk PRIMARY KEY (account_id)
);

ALTER TABLE public.bank_accounts ADD CONSTRAINT bank_accounts_fk FOREIGN KEY (account_branch_id) REFERENCES public.bank_branches(branch_id);



CREATE TABLE public.bank_transactions (
	transaction_id uuid NOT NULL,
	account_id uuid NOT NULL,
	transaction_account_id_to uuid NULL,
	transaction_amount numeric NOT NULL,
	transaction_type varchar(255) NOT NULL,
	transaction_date timestamptz(0) NOT NULL,
	CONSTRAINT bank_transactions_pk PRIMARY KEY (transaction_id)
);

ALTER TABLE public.bank_transactions ADD CONSTRAINT bank_transactions_fk FOREIGN KEY (account_id) REFERENCES public.bank_accounts(account_id);
ALTER TABLE public.bank_transactions ADD CONSTRAINT bank_transactions_fk_1 FOREIGN KEY (transaction_account_id_to) REFERENCES public.bank_accounts(account_id);


