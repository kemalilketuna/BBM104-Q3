import os

folder = 'BBM104_S24_Q3_Sample_IO_v1'

test_inputs = []
test_outputs = []

# list all files in the folder
files = os.listdir(folder)
for file in files:
    if file[0] == 'i':
        test_inputs.append(file)
    elif file[0] == 'o':
        test_outputs.append(file)

# sort the lists
test_inputs.sort()
test_outputs.sort()

os.system('javac BNF.java')

for i in range(len(test_inputs)):
    input_file = folder + '/' + test_inputs[i]
    output_file = folder + '/' + test_outputs[i]
    os.system('java BNF ' + input_file + ' output.txt')
    os.system('diff output.txt ' + output_file)
    print('Test', i+1, 'completed')