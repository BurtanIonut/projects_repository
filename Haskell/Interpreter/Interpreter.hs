
---Given a Program ADT, implement an Interpreter for it---


-------------------------------------------------------------------------------
---------------------------------- The Prog ADT -------------------------------
-------------------------------------------------------------------------------
data Asgn = Asgn String Expr deriving (Show, Read)

data Prog = Eq Asgn
          | Seq Prog Prog
          | If Expr Prog Prog
          | For Asgn Expr Asgn Prog
          | Assert Expr
          | Return Expr deriving (Show, Read)

-------------------------------------------------------------------------------
-------------------------------- The Interpreter ------------------------------
-------------------------------------------------------------------------------

evalAdt :: Prog -> Either String Int
evalAdt a = prog a (Right None)

---------------------
---------------------

data Dict = None | Put (String,Int) Dict deriving Show

insert (a,(Left b)) _ _ = Left (Left b)
insert (a,(Right b)) None q = Right (Put (a,b) q)
insert (a,(Right b)) (Put (c,d) q) acc = if a == c
                                  then insert (a,(Right b)) q acc
                                  else insert (a,(Right b)) q (Put (c,d) acc)
get None a = Left "Uninitialized variable"
get (Put (s,v) q) a = if a == s then Right v else get q a

----------------------

ret exp dic = eval exp dic

-----------------------

asg str exp dic = insert (str,(eval exp dic)) dic None

add _ (Left a) = Left a
add (Left a) _ = Left a
add (Right a) (Right b) = Right (a+b)
sub _ (Left a) = Left a
sub (Left a) _ = Left a
sub (Right a) (Right b) = Right (a-b)
mul _ (Left a) = Left a
mul (Left a) _ = Left a
mul (Right a) (Right b) = Right (a*b)
small _ (Left a) = Left a
small (Left a) _ = Left a
small (Right a) (Right b) = Right (if a < b then 1 else 0)
equal _ (Left a) = Left a
equal (Left a) _ = Left a
equal (Right a) (Right b) = Right (if a == b then 1 else 0)

asert b dict = case b of
       (Right a) -> if a == 1 then (Right dict) else (Left (Left "Assert failed"))
       (Left a) -> (Left (Left a))

-----------------------

eval expr dic = case expr of
        (Add a b) -> add (eval a dic) (eval b dic)
        (Sub a b) -> sub (eval a dic) (eval b dic)
        (Mult a b) -> mul (eval a dic) (eval b dic)
        (Smaller a b) -> small (eval a dic) (eval b dic)
        (Equal a b) -> equal (eval a dic) (eval b dic)
        (Value a) -> (Right a)
        (Symbol a) -> get dic a

itrt cond (Asgn str exp) prog dict = case dict of
                                        (Right dct) -> case (eval cond dct) of
                                                       (Right val) -> if val == 1 then case (newDict prog dct) of
                                                                                       (Right dc) -> (itrt cond (Asgn str exp) prog (asg str exp dc))
                                                                                       (Left err) -> (Left err)
                                                                                  else (Right dct)
                                                       (Left err) -> (Left (Left err))
                                        (Left err) -> (Left err)
solveFor (Asgn str exp) cond pas prog dict = case (asg str exp dict) of
                                            (Right dct) -> itrt cond pas prog (Right dct)
                                            (Left done) -> (Left done)
-----------------------

newDict pr dict = case pr of
                  (Seq p1 p2) -> case (newDict p1 dict) of
                                 (Right dct) -> newDict p2 dct
                                 (Left err) -> (Left err)
                  (Eq (Asgn a b)) -> asg a b dict
                  (Assert a) -> asert (eval a dict) dict
                  (For asg cond inc prog) -> solveFor asg cond inc prog dict
                  (Return exp) -> case (eval exp dict) of
                                  (Left err) -> Left (Left err)
                                  (Right gut) -> Left (Right gut)
                  (If cond thn els) -> case (eval cond dict) of
                                       (Right cnd) -> if (cnd == 1) then (newDict thn dict) else (newDict els dict)
                                       (Left a) -> Left (Left a)
----------------------

prog pr dict = case dict of
			   (Left done) -> case done of
			                Left err -> Left err
			                Right gud -> Right gud
			   (Right dct) -> case pr of
                            Return a -> (eval a dct)
                            Seq a b -> prog b (newDict a dct)
                            _ -> Left "Missing return"
-----------------------
