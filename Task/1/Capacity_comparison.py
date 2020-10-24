
import matplotlib.pyplot as plt



C = [10,15,17,20,30,40,50]
C_Splittable = [ 99999, 174.13961461928812, 157.9048878065381, 155.51358363486762, 155.51358363486762, 155.51358363486762, 155.51358363486762]
C_Unsplittable = [99999, 186.5329006995025, 162.687496149879,  155.51358363486762, 155.51358363486762, 155.51358363486762, 155.51358363486762]


fig = plt.figure()
font = {'family' : 'sans', 'size'   : 12}
plt.rc('font', **font)

plt.plot(C[1:], C_Splittable[1:], 'bo-', color='red', label='Splittable')
plt.plot(C[1:], C_Unsplittable[1:], 'bo-', color='blue', label='Unsplittable')
#plt.yscale('log')
plt.title('CAPACITY COMPARISON')
plt.xlabel('Link Capacity')
plt.ylabel('Total carried traffic')
plt.grid(which='major', axis='both', linestyle='--')

for x_,y_ in zip(C[1:],C_Splittable[1:]):
    label = round(y_, 2)
    plt.annotate(label, # this is the text
                 (x_,y_), # this is the point to label
                 textcoords="offset points", # how to position the text
                 xytext=(0,10), # distance from text to points (x,y)
                 ha='center',
                 rotation = 0,
                 fontsize = 8) # horizontal alignment can be left, right or center

for x_,y_ in zip(C[1:],C_Unsplittable[1:]):
    label = round(y_, 2)
    plt.annotate(label, # this is the text
                 (x_,y_), # this is the point to label
                 textcoords="offset points", # how to position the text
                 xytext=(0,10), # distance from text to points (x,y)
                 ha='center',
                 rotation = 0,
                 fontsize = 8) # horizontal alignment can be left, right or center


plt.legend()
plt.show()




